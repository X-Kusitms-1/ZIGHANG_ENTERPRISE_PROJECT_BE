package com.project.zighang.global.exception.template;

import com.project.zighang.global.exception.ApiResponseCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RspTemplate<T> {

    private final int statusCode;
    private final String message;
    private final T data;

    // --- 성공 응답 ---

    // 데이터가 없는 성공 응답 (e.g., 삭제 성공)
    public static RspTemplate<Void> success(ApiResponseCode code) {
        return new RspTemplate<>(code.getHttpStatusCode(), code.getMessage(), null);
    }

    // 데이터가 있는 성공 응답
    public static <T> RspTemplate<T> success(ApiResponseCode code, T data) {
        return new RspTemplate<>(code.getHttpStatusCode(), code.getMessage(), data);
    }

    // --- 에러 응답 ---

    // 정적 에러 메시지를 사용하는 에러 응답
    public static RspTemplate<Void> error(ApiResponseCode code) {
        return new RspTemplate<>(code.getHttpStatusCode(), code.getMessage(), null);
    }

    // 동적인 에러 메시지를 사용하는 에러 응답 (e.g., Validation 에러)
    public static RspTemplate<Void> error(ApiResponseCode code, String message) {
        return new RspTemplate<>(code.getHttpStatusCode(), message, null);
    }
}