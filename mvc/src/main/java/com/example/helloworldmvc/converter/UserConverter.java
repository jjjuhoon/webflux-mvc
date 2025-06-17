package com.example.helloworldmvc.converter;

import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.domain.enums.Role;
import com.example.helloworldmvc.web.dto.GoogleDetailResponse;
import com.example.helloworldmvc.web.dto.TokenDTO;
import com.example.helloworldmvc.web.dto.TokenListDTO;
import com.example.helloworldmvc.web.dto.UserResponseDTO;

import java.util.List;

public class UserConverter {

    public static User toGoogleUser(GoogleDetailResponse googleProfile){
        String name = (googleProfile.getName() != null ? googleProfile.getName() : "Google User"); // 기본 이름 설정
        return User.builder()
                .email(googleProfile.getEmail())
                .name(name)
                .role(Role.USER)
                .build();
    }
    public static User toGoogleUser(String email){
        return User.builder()
                .email(email)
                .name("Google User")
                .role(Role.USER)
                .build();
    }
    public static UserResponseDTO.OAuthResponse toOAuthResponse(TokenDTO accessToken, TokenDTO refreshToken, Boolean isLogin, User user) {
        return UserResponseDTO.OAuthResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .isLogin(isLogin)
                .email(user.getEmail())
                .build();
    }

    public static TokenListDTO toTokenList(List<TokenDTO> tokens){
        return TokenListDTO.builder()
                .tokenList(tokens)
                .build();
    }
}
