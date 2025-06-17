package com.example.helloworldmvc.web.controller;

import com.example.helloworldmvc.apiPayload.ApiResponse;
import com.example.helloworldmvc.config.auth.JwtTokenProvider;
import com.example.helloworldmvc.service.GoogleService;
import com.example.helloworldmvc.service.UserService;
import com.example.helloworldmvc.web.dto.GoogleDetailResponse;
import com.example.helloworldmvc.web.dto.TokenDTO;
import com.example.helloworldmvc.web.dto.TokenListDTO;
import com.example.helloworldmvc.web.dto.UserRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/google")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GoogleService googleService;
    private final JwtTokenProvider jwtTokenProvider;

    // 구글 로그인 화면 이동
    @PostMapping("/login-view")
    @Operation(summary = "구글 로그인 화면 조회 API", description = "구글 간편 로그인 페이지로 이동")
    public ResponseEntity<ApiResponse<String>> getGoogleLoginView() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .body(googleService.getGoogleLoginView());
    }

    @Operation(summary = "구글 로그인 API", description = "구글 로그인 및 회원 가입을 진행")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameters({
            @Parameter(name = "token", description = "query string(RequestParam) - accessToken 입력"),
    })
    @GetMapping("/login")
    public ApiResponse<TokenListDTO> googleLoginInfo(@RequestParam(value = "token") String token) {
        return ApiResponse.onSuccess(googleService.loginGoogle(token));
    }

    @Operation(summary = "구글 로그인 인증코드 발급 API", description = "code 발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping("/code")
    public ApiResponse<String> grantGoogleLoginCode(@RequestParam(value = "code") String code,
                                                    HttpServletResponse httpServletResponse) {
        httpServletResponse.addCookie(new Cookie("code", code));
        return ApiResponse.onSuccess(code);
    }
    @Operation(summary = "이메일 로그인 API", description = "이메일을 사용한 회원 가입한 사용자 로그인 진행")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameters({
    })
    @PostMapping("/login-email")
    public ApiResponse<TokenListDTO> loginGoogleEmail(@RequestBody @Valid UserRequestDTO.GoogleEmailRequest googleEmailRequest) {
        return ApiResponse.onSuccess(userService.loginGmail(googleEmailRequest));
    }

//    @Operation(summary = "모바일 구글 로그인 API", description = "구글 로그인 및 회원 가입을 진행")
//    @ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
//    })
//    @Parameters({
//            @Parameter(name = "token", description = "query string(RequestParam) - accessToken 입력"),
//    })
//    @PostMapping("/login-mobile")
//    public ApiResponse<List<TokenDTO>> mobileGoogleLoginInfo(@RequestParam(value = "token") String token) throws GeneralSecurityException, IOException {
//        return ApiResponse.onSuccess(googleService.loginGoogleMobile(token));
//    }
//
//    // 구글 로그인 이후 ID 토큰을 발급받는  redirection url
//    @GetMapping("/token")
//    public ResponseEntity<String> handleGoogleLoginCallback(
//            @RequestParam String code
//    ) throws IOException {
//        String token = googleService.getIdTokenFromGoogle(code);
//        return ResponseEntity.ok(token);
//    }

}
