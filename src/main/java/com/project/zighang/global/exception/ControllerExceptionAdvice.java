package com.project.zighang.global.exception;

import com.project.zighang.global.template.RspTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

public class ControllerExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<RspTemplate> handleCustomException(CustomException e , WebRequest request) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(RspTemplate.error(e.getError(), e.getMessage()));
    }

}
