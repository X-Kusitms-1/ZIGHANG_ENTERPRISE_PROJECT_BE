package com.project.zighang.global.exception;

import org.springframework.http.HttpStatus;

public interface ApiResponseCode {
    HttpStatus getHttpStatus();
    String getMessage();

    default int getHttpStatusCode() {
        return getHttpStatus().value();
    }
}
