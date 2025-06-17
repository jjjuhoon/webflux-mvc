package com.example.helloworldmvc.service;

import com.example.helloworldmvc.apiPayload.GeneralException;
import com.example.helloworldmvc.apiPayload.code.status.ErrorStatus;
import com.example.helloworldmvc.config.auth.JwtTokenProvider;
import com.example.helloworldmvc.converter.UserConverter;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.repository.UserRepository;
import com.example.helloworldmvc.web.dto.TokenDTO;
import com.example.helloworldmvc.web.dto.TokenListDTO;
import com.example.helloworldmvc.web.dto.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.helloworldmvc.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenListDTO loginGmail(UserRequestDTO.GoogleEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        TokenDTO accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        redisTemplate.opsForValue().set("RT:" + user.getEmail(), refreshToken.getToken(), refreshToken.getTokenExpriresTime().getTime(), TimeUnit.MILLISECONDS);
        List<TokenDTO> tokenDTOList = new ArrayList<>();
        tokenDTOList.add(refreshToken);
        tokenDTOList.add(accessToken);

        return UserConverter.toTokenList(tokenDTOList);

    }


    @Override
    public String findLanguage(String gmail) {
        return userRepository.findByEmail(gmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getUserLanguageList()
                .stream()
                .reduce((first, second) -> second) // 가장 최근의 UserLanguage 객체를 가져옴
                .map(userLanguage -> userLanguage.getLanguage().getName()) // Language 이름 반환
                .orElseThrow(() -> new IllegalArgumentException("No language found for user"));
    }

}
