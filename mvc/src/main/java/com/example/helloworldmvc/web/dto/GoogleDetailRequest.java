package com.example.helloworldmvc.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoogleDetailRequest {

    private String id_token;
}
