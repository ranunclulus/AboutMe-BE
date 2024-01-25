package com.example.aboutme.apiPayload.code.status;

import com.example.aboutme.apiPayload.code.BaseErrorCode;
import com.example.aboutme.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 일반적인 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 마이스페이스 에러
    _UNVALID_CHARACTER_TYPE(HttpStatus.BAD_REQUEST, "SPACE400", "캐릭터 타입은 1부터 9까지의 숫자만 가능합니다."),
    _UNVALID_ROOM_TYPE(HttpStatus.BAD_REQUEST, "SPACE401", "방 타입은 1부터 4까지의 숫자만 가능합니다."),

    // 마이프로필 에러
    PROFILE_SIZE_OVERFLOW(HttpStatus.BAD_REQUEST, "PROFILE400", "이 이상 마이프로필을 생성할 수 없습니다"),

    // 멤버 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER400", "해당하는 사용자가 존재하지 않습니다");

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
                .build();
    }
}
