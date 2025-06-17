package com.example.helloworldmvc.service;

import com.example.helloworldmvc.apiPayload.ApiResponse;
import com.example.helloworldmvc.config.auth.GoogleClient;
import com.example.helloworldmvc.config.auth.JwtTokenProvider;
import com.example.helloworldmvc.converter.UserConverter;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.repository.UserRepository;
import com.example.helloworldmvc.web.dto.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleServiceImpl implements GoogleService {

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.mobile.id}")
    private String googleMobileClientId;

    @Value("${google.client.pw}")
    private String googleClientPassword;

    @Value("${google.login.url}")
    private String googleApiUrl;

    @Value("${google.redirect.url}")
    private String redirectUrl;

    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile"
    );
    private final GoogleClient googleClient;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    public ApiResponse<String> getGoogleLoginView() {
        return ApiResponse.<String>builder()
                .isSuccess(true)
                .code("200")
                .result(googleApiUrl + "client_id=" + googleClientId
                        + "&redirect_uri=" + redirectUrl
                        + "&response_type=code"
                        + "&scope=email%20profile%20openid"
                        + "&access_type=offline")
                .message("google login view url입니다.")
                .build();
    }

    @Override
    @Transactional
    public TokenListDTO loginGoogle(String token) {
        String decodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8);
//        GoogleTokenResponse googleTokenResponse = googleClient.getGoogleToken(GoogleTokenRequest.builder()
//                .clientId(googleMobileClientId)
//                .clientSecret(googleClientPassword)
//                .code(decodedCode)
//                .redirectUri(redirectUrl)
//                .grantType("authorization_code")
//                .build());
        GoogleDetailResponse googleProfile = googleClient.getGoogleDetailInfo(GoogleDetailRequest.builder()
                .id_token(decodedToken)
                .build());
        Optional<User> optionalUser = userRepository.findByEmail(googleProfile.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            TokenDTO accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
            TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
            redisTemplate.opsForValue().set("RT:" + user.getEmail(), refreshToken.getToken(), refreshToken.getTokenExpriresTime().getTime(), TimeUnit.MILLISECONDS);
            List<TokenDTO> tokenDTOList = new ArrayList<>();
            tokenDTOList.add(refreshToken);
            tokenDTOList.add(accessToken);

            return UserConverter.toTokenList(tokenDTOList);
        } else {
            User user = userRepository.save(UserConverter.toGoogleUser(googleProfile));
            TokenDTO accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
            TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
            redisTemplate.opsForValue().set("RT:" + refreshToken, TimeUnit.MILLISECONDS);

            List<TokenDTO> tokenDTOList = new ArrayList<>();
            tokenDTOList.add(refreshToken);
            tokenDTOList.add(accessToken);

            return UserConverter.toTokenList(tokenDTOList);
        }
    }

    @Override
    public List<TokenDTO> loginGoogleMobile(String token) throws GeneralSecurityException, IOException {
        // Step 1: id_token을 검증합니다.
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(googleMobileClientId))  // Client ID 설정
                .build();
        GoogleIdToken googleIdToken;
        try {
            googleIdToken = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.info("[ERROR] : ", e);
            throw new IllegalArgumentException("Wrong In verify IdToken", e);
        }
        // Step 2: 토큰 검증 성공 시 사용자 정보를 추출합니다.
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();

        // Step 3: 사용자 정보를 통해 데이터베이스에서 사용자 조회
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Step 4: 사용자가 이미 존재하는지 확인 후, 토큰 생성 및 반환
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            TokenDTO accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
            TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            // Redis에 refresh token 저장
            redisTemplate.opsForValue().set("RT:" + user.getEmail(), refreshToken.getToken(), refreshToken.getTokenExpriresTime().getTime(), TimeUnit.MILLISECONDS);

            List<TokenDTO> tokenDTOList = new ArrayList<>();
            tokenDTOList.add(refreshToken);
            tokenDTOList.add(accessToken);

            return tokenDTOList;
        } else {
            // Step 5: 새로운 사용자일 경우 회원가입 처리 후 토큰 발급
            User user = userRepository.save(UserConverter.toGoogleUser(email));
            TokenDTO accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
            TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            // Redis에 refresh token 저장
            redisTemplate.opsForValue().set("RT:" + user.getEmail(), refreshToken.getToken(), refreshToken.getTokenExpriresTime().getTime(), TimeUnit.MILLISECONDS);

            List<TokenDTO> tokenDTOList = new ArrayList<>();
            tokenDTOList.add(refreshToken);
            tokenDTOList.add(accessToken);

            return tokenDTOList;
        }
    }

    @Override
    public String getIdTokenFromGoogle(String code) throws IOException {
        return getTokenResponseFromGoogle(code).getIdToken();
    }

    private com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse getTokenResponseFromGoogle(String code) throws IOException {
        return buildGoogleAuthorizationCodeFlow()
                .newTokenRequest(code)
                .setRedirectUri(redirectUrl)
                .execute();
    }
    private GoogleAuthorizationCodeFlow buildGoogleAuthorizationCodeFlow() {
        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                googleClientId, googleClientPassword, SCOPES)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();
    }
}
