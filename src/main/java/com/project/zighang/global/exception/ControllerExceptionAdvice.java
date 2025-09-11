package com.project.zighang.global.exception;

import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.global.exception.template.RspTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(CustomException e, WebRequest request) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(RspTemplate.error(e.getError(), e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<?> handleNoSuchElementException(NotFoundException e, WebRequest request) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(RspTemplate.error(e.getError(), e.getMessage()));
    }
}
