package com.project.zighang.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Error implements ApiResponseCode {

    /**
     * 400 BAD REQUEST EXCEPTION
     */
    BAD_CLIENT_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 API 요청입니다."),

    /**
     * 404 NOT FOUND
     */
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자 정보입니다."),

    /**
     * Redis Error
     */
    REDIS_SET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에 값을 저장하는 데 실패했습니다."),
    REDIS_GET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에서 값을 가져오는 데 실패했습니다."),
    REDIS_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에서 값을 삭제하는 데 실패했습니다."),
    SHA256_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SHA256 해시 생성에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
