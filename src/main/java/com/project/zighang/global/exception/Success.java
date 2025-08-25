package com.project.zighang.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Success implements ApiResponseCode {

    /**
     * 200 OK
     */
    GET_API_REQUEST_SUCCESS(HttpStatus.OK, "Get API 호출에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
