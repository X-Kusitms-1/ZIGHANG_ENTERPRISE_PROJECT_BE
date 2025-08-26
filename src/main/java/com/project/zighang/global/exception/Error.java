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
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자 정보입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
