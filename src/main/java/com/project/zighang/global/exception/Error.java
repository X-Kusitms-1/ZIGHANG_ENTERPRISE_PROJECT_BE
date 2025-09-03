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
    BAD_REQUEST_APPLY_COUNT_VALUE(HttpStatus.BAD_REQUEST, "오늘의 공고 개수는 0개 이상이어야 합니다."),
    BAD_REQUEST_CAREER_VALUE(HttpStatus.BAD_REQUEST, "잘못된 커리어 데이터입니다."),

    /**
     * 404 NOT FOUND
     */
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자 정보입니다."),
    NOT_FOUND_USER_ONBOARDING(HttpStatus.NOT_FOUND, "존재하지 않는 사용자 온보딩 정보입니다."),

    /**
     * Redis Error
     */
    REDIS_SET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에 값을 저장하는 데 실패했습니다."),
    REDIS_GET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에서 값을 가져오는 데 실패했습니다."),
    REDIS_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에서 값을 삭제하는 데 실패했습니다."),
    REDIS_EXISTS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에서 키 존재 여부 확인에 실패했습니다."),
    SHA256_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SHA256 해시 생성에 실패했습니다."),

    /**
     * SubsctiptionError
     */
    ALREADY_SUBSCRIBED_COMPANY(HttpStatus.BAD_REQUEST, "이미 구독하고 있는 기업입니다."),
    SUBSCRIPTION_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "구독 중 오류가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
