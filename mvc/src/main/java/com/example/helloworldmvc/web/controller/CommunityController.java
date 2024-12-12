package com.example.helloworldmvc.web.controller;

import com.example.helloworldmvc.apiPayload.ApiResponse;
import com.example.helloworldmvc.config.auth.JwtTokenProvider;
import com.example.helloworldmvc.service.CommentService;
import com.example.helloworldmvc.service.CommunityService;
import com.example.helloworldmvc.web.dto.CommentRequestDTO;
import com.example.helloworldmvc.web.dto.CommentResponseDTO;
import com.example.helloworldmvc.web.dto.CommunityRequestDTO;
import com.example.helloworldmvc.web.dto.CommunityResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CommunityService communityService;
    private final CommentService commentService;

    @PostMapping(value = "/{category_id}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "커뮤니티 글 작성 API", description = "커뮤니티 게시판에 글을 작성하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다.")
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
            @Parameter(name = "category_id", description = "PathVariable - 게시글 카테고리 아이디"),
    })
    public ApiResponse<CommunityResponseDTO.CreatedPostDTO> createCommunity(@RequestHeader(name = "Authorization") String accessToken,
                                                                            @PathVariable(name = "category_id") Long categoryId,
                                                                            @ModelAttribute @Valid CommunityRequestDTO.CreatePostDTO request) {
        String gmail = jwtTokenProvider.getGoogleEmail(accessToken);
        return ApiResponse.onSuccess(communityService.createCommunityPost(gmail, categoryId, request));
    }

    @GetMapping(value = "/{category_id}/create")
    @Operation(summary = "커뮤니티 글 목록 조회 API", description = "해당 카테고리 게시글 목록을 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
            @Parameter(name = "category_id", description = "PathVariable - 게시글 카테고리 아이디"),
            @Parameter(name = "page", description = "query string(RequestParam) - 몇번째 페이지인지 가리키는 page 변수 입니다! (0부터 시작)"),
            @Parameter(name = "size", description = "query string(RequestParam) - 몇 개씩 불러올지 개수를 세는 변수입니다. (1 이상 자연수로 설정)")
    })
    public ApiResponse<CommunityResponseDTO.PostListDTO> getCommunityList(@RequestHeader(name = "Authorization") String accessToken,
                                                                          @PathVariable(name = "category_id") Long categoryId,
                                                                          @RequestParam(name = "page") Integer page,
                                                                          @RequestParam(name = "size") Integer size) {
        String gmail = jwtTokenProvider.getGoogleEmail(accessToken);
        return ApiResponse.onSuccess(communityService.getCommunityList(gmail, categoryId, page, size));
    }

    @PostMapping(value = "/{category_id}/detail/{community_id}")
    @Operation(summary = "커뮤니티 글 상세 조회 API", description = "커뮤니티 게시글을 상세 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMUNITY4001", description = "커뮤니티 글이 존재하지 않습니다."),
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
            @Parameter(name = "category_id", description = "PathVariable - 게시글 카테고리 아이디"),
            @Parameter(name = "community_id", description = "PathVariable - 게시글 아이디"),
            @Parameter(name = "page", description = "query string(RequestParam) - 몇번째 페이지인지 가리키는 page 변수 입니다! (0부터 시작)"),
            @Parameter(name = "size", description = "query string(RequestParam) - 몇 개씩 불러올지 개수를 세는 변수입니다. (1 이상 자연수로 설정)")
    })
    public ApiResponse<CommunityResponseDTO.PostDetailDTO> getCommunityDetail(@RequestHeader(name = "Authorization") String accessToken,
                                                                           @PathVariable(name = "category_id") Long categoryId,
                                                                           @PathVariable(name = "community_id") Long communityId,
                                                                           @RequestParam(name = "page") Integer page,
                                                                           @RequestParam(name = "size") Integer size) {
        String gmail = jwtTokenProvider.getGoogleEmail(accessToken);
        return ApiResponse.onSuccess(communityService.getCommunityDetail(gmail, communityId, page, size));
    }


    @PostMapping(value = "/{community_id}/comment")
    @Operation(summary = "커뮤니티 댓글 작성 API", description = "커뮤니티 게시판에 글을 작성하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을수 없습니다.")
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "RequestHeader - 로그인한 사용자 토큰"),
    })
    public ApiResponse<CommentResponseDTO.commentCreateRes> createComment(@RequestHeader(name = "Authorization") String accessToken,
                                                                       @PathVariable(name = "community_id") Long communityId,
                                                                       @RequestBody @Valid CommentRequestDTO.commentCreateReq requestBody) {
        {
        String gmail = jwtTokenProvider.getGoogleEmail(accessToken);
        return ApiResponse.onSuccess(commentService.createComment(gmail, communityId, requestBody));
    }

    }
}
