package com.example.helloworldmvc.web.controller;

import com.example.helloworldmvc.apiPayload.ApiResponse;
import com.example.helloworldmvc.config.auth.JwtTokenProvider;
import com.example.helloworldmvc.converter.CenterConverter;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.service.CommentService;
import com.example.helloworldmvc.service.LanguageService;
import com.example.helloworldmvc.web.dto.CenterRequestDTO;
import com.example.helloworldmvc.web.dto.CenterResponseDTO;
import com.example.helloworldmvc.web.dto.LanguageRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT Token")
@RestController
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class InitialController {
    private final JwtTokenProvider jwtTokenProvider;
    private final LanguageService languageService;

    @PostMapping("/language/{languageId}")
    @Operation(summary = "사용자 언어 설정 API", description = """
            사용자가 1~5까지의 정수값으로 언어를 설정합니다.
            1 : English
            2 : Korean
            3 : Japanese
            4 : Chinese
            5 : Vietnamese
            """)
    public ApiResponse<String> setUserLanguage(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("languageId") Long languageId) {

        String userId = jwtTokenProvider.getGoogleEmail(accessToken);
        return ApiResponse.onSuccess(languageService.setUserLanguage(userId, languageId));
    }
}
