package com.project.zighang.domain.subscription.exception.model;

import com.project.zighang.global.exception.CustomException;
import com.project.zighang.global.exception.Error;

public class AlreadyExistException extends CustomException {
    public AlreadyExistException(Error error, String message) {
        super(error, message);
    }
}
