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
    GET_API_REQUEST_SUCCESS(HttpStatus.OK, "Get API 호출에 성공했습니다."),
    UNSUBSCRIBE_SUCCESS(HttpStatus.OK, "정상적으로 구독이 취소됐습니다."),
    GET_SUBSCRIPTION_INFO_SUCCESS(HttpStatus.OK, "정상적으로 구독 정보를 불러왔습니다."),

    /**
     * 201 CREATED
     */
    POST_USER_Onboarding_API_REQUEST_SUCCESS(HttpStatus.CREATED, "유저 온보딩 POST API 호출에 성공했습니다."),
    CREATE_JWT_TOKEN_SUCCESS(HttpStatus.CREATED, "소셜 로그인 성공 및 JWT 토큰 정상 발급했습니다."),
    SUBSCRIBE_SUCCESS(HttpStatus.CREATED, "정상적으로 구독됐습니다.");




    private final HttpStatus httpStatus;
    private final String message;
}
