package Helloworld.helloworld_webflux.config.auth;

import Helloworld.helloworld_webflux.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;

@Component
@Slf4j
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;
    // 객체 초기화, secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    // 토큰에서 회원정보 추출 - email (payload의 subject)
    public String getGoogleEmail(String token) {
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getToken(ServerHttpRequest request){
        return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    public String resolveToken(ServerHttpRequest request){
        String bearerToken = getToken(request);

        if(!StringUtils.isBlank(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }

        return null;
    }

    public boolean validateJwtToken(String authToken) throws AuthenticationException {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new JwtException("Invalid JWT signature: "+e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new JwtException("JWT token is expired: "+e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new JwtException("JWT token is unsupported: "+e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new JwtException("JWT claims string is empty: "+e.getMessage());
        }catch (JwtException e){
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token: "+e.getMessage());
        }
    }

    public Authentication getAuthentication(String accessToken) throws AuthenticationException {
        Claims claims = parseClaims(accessToken);

        if(claims.get("types") == null){
            throw new AuthenticationException("Token without permission information") {
            };
        }
        String gmail = getGoogleEmail(accessToken);
        String role = getRole(accessToken);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role);
        return new CustomAuthenticationToken(gmail, null, role, Collections.singleton(authority));
    }

    private Claims parseClaims(String accessToken){
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
        } catch(ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    // 토큰에서 role 추출
    public String getRole(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("만료된 Access 토큰입니다.");
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 Access 토큰입니다.");
        }
    }
}