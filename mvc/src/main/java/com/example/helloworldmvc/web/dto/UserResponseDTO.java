package com.example.helloworldmvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuthResponse {
        Boolean isLogin;
        TokenDTO accessToken;
        TokenDTO refreshToken;
        String email;
    }
}
