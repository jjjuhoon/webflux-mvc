package com.example.helloworldmvc.web.controller;

import com.example.helloworldmvc.apiPayload.ApiResponse;
import com.example.helloworldmvc.config.auth.JwtTokenProvider;
import com.example.helloworldmvc.converter.CenterConverter;
import com.example.helloworldmvc.domain.Center;
import com.example.helloworldmvc.domain.Counselor;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.service.CenterService;
import com.example.helloworldmvc.web.dto.CenterRequestDTO;
import com.example.helloworldmvc.web.dto.CenterResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/center")
public class CenterController {

    private final CenterService centerService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/")
    @Operation(summary = "센터 조회 API", description = "지도에서 센터 위치와 정보를 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CENTER4001", description = "센터를 찾을수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
            @Parameter(name = "page", description = "query string(RequestParam) - 몇번째 페이지인지 가리키는 page 변수 (0부터 시작)"),
            @Parameter(name = "size", description = "query string(RequestParam) - 몇 개씩 불러올지 개수를 세는 변수 (1 이상 자연수로 설정)")
    })
    public ApiResponse<CenterResponseDTO.CenterMapListRes> getCenter( @RequestHeader("Authorization") String accessToken,
                                                                      @RequestParam("latitude") double latitude,
                                                                      @RequestParam("longitude") double longitude,
                                                                      @RequestParam(name = "page") Integer page,
                                                                      @RequestParam(name = "size") Integer size)  {
        String userId = jwtTokenProvider.getGoogleEmail(accessToken);
        Page<Center> centerList = centerService.getCenterListByDistance(latitude,longitude, page, size);
        return ApiResponse.onSuccess(CenterConverter.toCenterMapListRes(centerList, userId));
    }

    @GetMapping("/{center_id}/reservation")
    @Operation(summary = "센터 예약하기 조회 API", description = "해당 센터의 언어에 맞는 상담사 리스트와 현재 날짜를 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CENTER4001", description = "센터를 찾을수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CENTER4002", description = "상담사를 찾을수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
            @Parameter(name = "page", description = "query string(RequestParam) - 몇번째 페이지인지 가리키는 page 변수 (0부터 시작)"),
            @Parameter(name = "size", description = "query string(RequestParam) - 몇 개씩 불러올지 개수를 세는 변수 (1 이상 자연수로 설정)")
    })
    public ApiResponse<CenterResponseDTO.CounselorListRes> getCenterReservation(@RequestHeader("Authorization") String accessToken,
                                                                                @PathVariable("center_id") Long centerId,
                                                                                @RequestParam(name = "page") Integer page,
                                                                                @RequestParam(name = "size") Integer size) {
        String userId = jwtTokenProvider.getGoogleEmail(accessToken);
        Page<Counselor> counselorList = centerService.getCounselorList(userId, centerId , page, size);
        return ApiResponse.onSuccess(CenterConverter.toCounselorListRes(counselorList,userId));
    }

    @PostMapping("/{center_id}/filter")
    @Operation(summary = "상담가능 언어 필터링 생성 API", description = "해당 센터의 상담가능한 상담사를 찾기 위해 필터링을 생성하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CENTER4001", description = "센터를 찾을수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4002", description = "설정 가능한 언어가 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
    })
    public ApiResponse<CenterResponseDTO.FilterRes> createLanguageFilter(@RequestHeader("Authorization") String accessToken,
                                                                         @PathVariable("center_id") Long centerId,
                                                                         @RequestBody @Valid CenterRequestDTO.FilterLanguageReq request) {
        String userId = jwtTokenProvider.getGoogleEmail(accessToken);
        User userLanguage = centerService.createUserLanguage(userId, centerId, request);
        return ApiResponse.onSuccess(CenterConverter.toFilterRes(userLanguage));
    }

    @GetMapping("/{center_id}/detail")
    @Operation(summary = "상담센터 상세정보 조회 API", description = "해당 센터의 상세정보를 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CENTER4001", description = "센터를 찾을수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4002", description = "설정 가능한 언어가 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
            @Parameter(name = "center_id", description = "PathVariable - 상담센터 아이디")
    })
    public ApiResponse<?> getCenterDetail(@RequestHeader("Authorization") String accessToken,
                                          @PathVariable("center_id") Long centerId) {
        String userId = jwtTokenProvider.getGoogleEmail(accessToken);
        Center center = centerService.getCenter(userId, centerId);
        return ApiResponse.onSuccess(CenterConverter.toCenterDetailRes(center,userId));
    }
}
