package Helloworld.helloworld_webflux.config;

import Helloworld.helloworld_webflux.config.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        // 경로별 인가 작업
        http.authorizeExchange((exchange) -> exchange
                        .pathMatchers("/chat/**").permitAll()
                        .pathMatchers("/api/v1/google/**").permitAll() // 인증 없이 접근 가능
                        .pathMatchers(HttpMethod.GET,
                                "webjars/**","/swagger-ui/*", "/v3/api-docs/swagger-config", "/api/logistics", "/v3/api-docs", "health", "apikeytest").permitAll()
                        .pathMatchers("/auth/users/**").hasRole("USER")
                        .pathMatchers("/auth/admin/**").hasRole("ADMIN")
                        .anyExchange().authenticated());// 나머지 경로는 인증 필요

        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers(headers -> headers.frameOptions(
                        ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        http
                .addFilterBefore(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        System.out.println("필터 실행");

        return http.build();
    }

    private AuthenticationWebFilter authenticationWebFilter() {
        ReactiveAuthenticationManager authenticationManager = Mono::just;

        AuthenticationWebFilter authenticationWebFilter
                = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
        return authenticationWebFilter;
    }

    private ServerAuthenticationConverter serverAuthenticationConverter(){
        return exchange -> {
            String token = jwtTokenProvider.resolveToken(exchange.getRequest());
            try {
                if(!Objects.isNull(token) && jwtTokenProvider.validateJwtToken(token)){
                    return Mono.justOrEmpty(jwtTokenProvider.getAuthentication(token));
                }
            } catch (AuthorizationDeniedException e) {
                log.error(e.getMessage(), e);
            }
            return Mono.empty();
        };
    }
}
