package com.example.helloworldmvc.web.dto;

import lombok.Getter;

public class UserRequestDTO {

    @Getter
    public static class GoogleEmailRequest{
        private String email;
    }
}
