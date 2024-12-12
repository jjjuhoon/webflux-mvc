package com.example.helloworldmvc.web.controller;

import com.example.helloworldmvc.apiPayload.ApiResponse;
import com.example.helloworldmvc.config.auth.JwtTokenProvider;
import com.example.helloworldmvc.converter.CenterConverter;
import com.example.helloworldmvc.converter.MyPageConverter;
import com.example.helloworldmvc.domain.Center;
import com.example.helloworldmvc.domain.Summary;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.domain.mapping.Reservation;
import com.example.helloworldmvc.service.CenterService;
import com.example.helloworldmvc.service.MyPageService;
import com.example.helloworldmvc.service.UserService;
import com.example.helloworldmvc.web.dto.CenterRequestDTO;
import com.example.helloworldmvc.web.dto.CenterResponseDTO;
import com.example.helloworldmvc.web.dto.MyPageRequestDTO;
import com.example.helloworldmvc.web.dto.MyPageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@SecurityRequirement(name = "JWT Token")
@RestController
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController {
    private final MyPageService myPageService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @GetMapping("/")
    @Operation(summary = "마이페이지 API", description = "마이페이지 화면 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다.")
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
    })
    public ApiResponse<MyPageResponseDTO.MyPageResDTO> getMyPage(@RequestHeader("Authorization") String accessToken) {
        String userId = jwtTokenProvider.getGoogleEmail(accessToken);
        User user = myPageService.getUser(userId);
        return ApiResponse.onSuccess(MyPageConverter.toMyPageRes(user));
    }

    @GetMapping("/allSummary")
    @Operation(summary = "전체 상담 조회 API", description = "(외국인)내 전체 상담 조회 화면 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다.")
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
            @Parameter(name = "page", description = "query string(RequestParam) - 몇번째 페이지인지 가리키는 page 변수 입니다! (0부터 시작)"),
            @Parameter(name = "size", description = "query string(RequestParam) - 몇 개씩 불러올지 개수를 세는 변수입니다. (1 이상 자연수로 설정)"),
    })
    public ApiResponse<?> getAllSummary(@RequestHeader("Authorization") String accessToken,
                                        @RequestParam(name = "page") Integer page,
                                        @RequestParam(name = "size") Integer size) {
        String userId = jwtTokenProvider.getGoogleEmail(accessToken);
        Page<Summary> summaryList = myPageService.getSummaryList(userId, page, size);
        return ApiResponse.onSuccess(MyPageConverter.toAllSummaryListRes(summaryList, userId));
    }

    @GetMapping("/detailSummary")
    @Operation(summary = "상세 상담 조회 API", description = "(외국인,상담사)상세 상담 조회 화면 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다.")
    })
    public ApiResponse<?> getDetailSummary(@RequestParam("summary-id") Long summaryId) {
        Summary summary = myPageService.getSummary(summaryId);

        return ApiResponse.onSuccess(MyPageConverter.toDetailSummaryRes(summary));
    }

    @GetMapping("/allReservation")
    @Operation(summary = "상담 신청 내역 조회 API", description = "(상담사)상담 신청 내역 조회 화면 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다.")
    })
    @Parameters({
            @Parameter(name = "counselor_id", description = "RequestHeader - 로그인한 상담사 아이디(accessToken으로 변경 예정)"),
            @Parameter(name = "page", description = "query string(RequestParam) - 몇번째 페이지인지 가리키는 page 변수 입니다! (0부터 시작)"),
            @Parameter(name = "size", description = "query string(RequestParam) - 몇 개씩 불러올지 개수를 세는 변수입니다. (1 이상 자연수로 설정)"),
    })
    public ApiResponse<?> getAllReservation(@RequestHeader("counselor_id") Long userId,
                                            @RequestParam(name = "page") Integer page,
                                            @RequestParam(name = "size") Integer size) {
        Page<Reservation> reservationList = myPageService.getReservationList(userId, page, size);
        return ApiResponse.onSuccess(MyPageConverter.toAllReservationListRes(reservationList, userId));
    }

    @PatchMapping(value = "/setProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 변경 API", description = "프로필 변경 API API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다."),
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
    })
    public ApiResponse<MyPageResponseDTO.PatchProfileEmail> createLanguageFilter(@RequestHeader("Authorization") String accessToken,
                                                                                 @ModelAttribute @Valid MyPageRequestDTO.PatchProfile request) {
        String userId = jwtTokenProvider.getGoogleEmail(accessToken);
        myPageService.setUserProfile(userId, request);
        return ApiResponse.onSuccess(MyPageResponseDTO.PatchProfileEmail.builder().email(userId).build());
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴 API", description = "회원을 탈퇴시키는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "NOT_FOUND, 회원정보가 존재하지 않습니다."),
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
    })
    public ApiResponse<String> deactivateUser(@RequestHeader(name = "Authorization") String accessToken) {
        String userId = jwtTokenProvider.getGoogleEmail(accessToken);
        return ApiResponse.onSuccess(myPageService.deactivateUser(userId));
    }

    @GetMapping("/AllMyCommunity")
    @Operation(summary = "내가 작성한 전체 글 조회 API", description = "내가 작성한 전체 글 조회 화면 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다.")
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
            @Parameter(name = "page", description = "query string(RequestParam) - 몇번째 페이지인지 가리키는 page 변수 입니다! (0부터 시작)"),
            @Parameter(name = "size", description = "query string(RequestParam) - 몇 개씩 불러올지 개수를 세는 변수입니다. (1 이상 자연수로 설정)"),
    })
    public ApiResponse<?> getAllMyCommunity(@RequestHeader("Authorization") String accessToken,
                                        @RequestParam(name = "page") Integer page,
                                        @RequestParam(name = "size") Integer size) {
        String gmail = jwtTokenProvider.getGoogleEmail(accessToken);
        return ApiResponse.onSuccess(myPageService.getCommunityList(gmail, page, size));
    }


    @GetMapping("/AllMyComment")
    @Operation(summary = "내가 작성한 전체 댓글 조회 API", description = "내가 작성한 전체 댓글 조회 화면 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다.")
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
            @Parameter(name = "page", description = "query string(RequestParam) - 몇번째 페이지인지 가리키는 page 변수 입니다! (0부터 시작)"),
            @Parameter(name = "size", description = "query string(RequestParam) - 몇 개씩 불러올지 개수를 세는 변수입니다. (1 이상 자연수로 설정)"),
    })
    public ApiResponse<?> getAllMyComment(@RequestHeader("Authorization") String accessToken,
                                            @RequestParam(name = "page") Integer page,
                                            @RequestParam(name = "size") Integer size) {
        String gmail = jwtTokenProvider.getGoogleEmail(accessToken);
        return ApiResponse.onSuccess(myPageService.getAllCommentsByUser(gmail, page, size));
    }
}
