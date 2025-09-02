package com.project.zighang.domain.subscription.exception.model;

import com.project.zighang.global.exception.CustomException;
import com.project.zighang.global.exception.Error;

public class SubscriptionInternalException extends CustomException {
    public SubscriptionInternalException(Error error, String message) {
        super(error, message);
    }
}
