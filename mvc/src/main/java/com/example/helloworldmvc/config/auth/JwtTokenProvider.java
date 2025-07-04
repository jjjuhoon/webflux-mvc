package com.example.helloworldmvc.config.auth;

import com.example.helloworldmvc.apiPayload.GeneralException;
import com.example.helloworldmvc.apiPayload.code.status.ErrorStatus;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.repository.UserRepository;
import com.example.helloworldmvc.web.dto.TokenDTO;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static com.example.helloworldmvc.apiPayload.code.status.ErrorStatus.INVALID_JWT;

@Slf4j
@RequiredArgsConstructor
@Component
@PropertySource(value = {"/application1.yml"})
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;

    // 객체 초기화, secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT Access 토큰 생성
    public TokenDTO createAccessToken(String email) {
        // 토큰 유효시간 30분
        long tokenValidTime = 2 * 60 * 60 * 1000L;
        Optional<User> user = userRepository.findByEmail(email);
        Claims claims = Jwts.claims().setSubject(email); // Claim : JWT payload에 저장되는 정보단위
        user.ifPresent(value -> claims.put("id", value.getId()));
        Date now = new Date();
        Date expiresTime = new Date(now.getTime() + tokenValidTime);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .claim("types", "atk")
                //.claim("userIdx",user.get().getUserIdx())
                .claim("role", user.get().getRole())
                .compact();
        return new TokenDTO(String.valueOf(TokenType.ATK), token, expiresTime);
    }

    // JWT Refresh 토큰 생성
    public TokenDTO createRefreshToken(String email) {
        // Refresh 토큰 유효시간 2주
        long tokenValidTime = 2 * 7 * 24 * 60 * 60 * 1000L;
        Optional<User> user = userRepository.findByEmail(email);
        Claims claims = Jwts.claims().setSubject(email); // JWT payload에 저장되는 정보단위
        user.ifPresent(value -> claims.put("id", value.getId()));
        Date now = new Date();
        Date expiresTime = new Date(now.getTime() + tokenValidTime); // 토큰 만료 시간
        String token = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(expiresTime) // Expire Time 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 signature 에 들어갈 secretkey 값 설정
                .claim("types", "rtk")
                .compact();
        return new TokenDTO(String.valueOf(TokenType.RTK), token, expiresTime);
    }

    // 토큰에서 사용자 정보를 추출하고 CustomAuthenticationToken을 생성
    public Authentication getAuthentication(String token) {
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String gmail = getGoogleEmail(token);
        String role = getRole(token);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role);
        return new CustomAuthenticationToken(gmail, null, role, Collections.singleton(authority));
    }
    // 토큰에서 회원정보 추출 - email (payload의 subject)
    public String getGoogleEmail(String token) {
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request 의 Header 에서 access token 값 추출 "atk" : "token--"
    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader("atk");
    }

    // 토큰 재발급 때 Header에 rtk를 넣어 요청, 나머지 경우 atk 사용
    public String resolveToken(HttpServletRequest request) {
        if (request.getHeader("Authorization") != null) {
            return request.getHeader("Authorization").substring(7);
        }
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken); // 토큰의  payload(claim)            // Access 토큰의 경우 redis 까지 검사
            if (claims.getBody().get("types").equals("atk")) {
                Object isLogOut = redisTemplate.opsForValue().get(jwtToken); // token 을 key 로 value 가져옴 (null 이면 유효 토큰, logout 이면 유효하지 않은 토큰)
                // 로그인 시 redis 에 email : refreshtoken 형태로 저장
                // 로그아웃 시 redis 에 accesstoken : logout 형태로 저장
                if (isLogOut != null) {
                    return false;
                }
                return !claims.getBody().getExpiration().before(new Date());// 만료안됐으면 true, 만료됐으면 false
            } else {
                // Refresh 토큰 유효성 검사
                return !claims.getBody().getExpiration().before(new Date()); // 만료안됐으면 true, 만료됐으면 false
            }
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 만료 시간
    public Date getExpireTime(String jwtToken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
        return claims.getBody().getExpiration();
    }

    // 토큰에서 회원정보 추출 - userIdx 추출
    public Long getCurrentUser(HttpServletRequest request) throws GeneralException { // userIdx 가져오기
        String jwtToken = resolveAccessToken(request); // Request의 header에서 Access 토큰 추출
        if (!validateToken(jwtToken)) {
            throw new GeneralException(INVALID_JWT);
        }
        Long userIdx = Long.valueOf(String.valueOf(Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwtToken)
                .getBody()
                .get("id")));

        return userIdx;
    }
    // 토큰에서 role 추출
    public String getRole(String token) {

        try {
            return Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (MalformedJwtException e) {
            throw new GeneralException(ErrorStatus.MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorStatus.EXPIRED_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_ACCESS_TOKEN);
        }
    }
}
