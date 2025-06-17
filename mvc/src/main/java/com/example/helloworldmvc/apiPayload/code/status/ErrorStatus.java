package com.example.helloworldmvc.apiPayload.code.status;


import com.example.helloworldmvc.apiPayload.code.BaseErrorCode;
import com.example.helloworldmvc.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum
ErrorStatus implements BaseErrorCode {


    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4000", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON4001", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON4003", "금지된 요청입니다."),

    // 사용자 관련 응답
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "사용자를 찾을수 없습니다."),
    LANGUAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4002", "설정 가능한 언어가 없습니다."),
    COUNSELOR_NOT_FOUND(HttpStatus.NOT_FOUND, "COUNSELOR4001", "상담사를 찾을수 없습니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "USER4003", "유효하지 않은 JWT입니다."),

    // 요약문 관련 응답
    SUMMARY_NOT_FOUND(HttpStatus.NOT_FOUND, "SUMMARY4001", "요약본을 찾을수 없습니다."),

    // 커뮤니티 관련 응답
    COMMUNITY_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY4001", "커뮤니티 글이 존재하지 않습니다."),


    // JWT 토큰 관련 응답
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001","유효하지 않은 Access 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4002","만료된 Access 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4003","만료된 Refresh 토큰입니다."),
    MALFORMED_JWT(HttpStatus.BAD_REQUEST, "TOKEN4004","잘못된 JWT 토큰입니다."),
    SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN4005","유효하지 않은 JWT 서명입니다."),

    // 센터 관련 응답
    CENTER_NOT_FOUND(HttpStatus.NOT_FOUND,"CENTER4001", "센터를 찾을수 없습니다."),
    LANGUAGE_NOT_MATCHING(HttpStatus.NOT_FOUND, "CENTER4003", "해당 언어의 상담사가 없습니다."),
    //글 관련 응답
    COMMUNITY_NOT_FOUND(HttpStatus.NOT_FOUND,"COMMUNITY4001","글을 찾을 수 없습니다"),
    //언어 관련 응답
    LANGUAGE_NOT_EXIST(HttpStatus.NOT_FOUND, "LANGUAGE4001", "해당 언어가 없습니다.");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;


    }
}

