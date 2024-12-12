package com.example.helloworldmvc.config.auth;

import com.example.helloworldmvc.web.dto.GoogleDetailRequest;
import com.example.helloworldmvc.web.dto.GoogleDetailResponse;
import com.example.helloworldmvc.web.dto.GoogleTokenRequest;
import com.example.helloworldmvc.web.dto.GoogleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "google", url = "${google.auth.url}")
public interface GoogleClient {

    @PostMapping("/token")
    GoogleTokenResponse getGoogleToken(GoogleTokenRequest googleTokenRequest);

    @PostMapping("/tokeninfo")
    GoogleDetailResponse getGoogleDetailInfo(GoogleDetailRequest googleDetailRequest);
}
