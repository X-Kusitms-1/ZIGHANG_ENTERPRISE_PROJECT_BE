package com.project.zighang.global.exception.model;

import com.project.zighang.global.exception.CustomException;
import com.project.zighang.global.exception.Error;

public class BadRequestException extends CustomException {
  public BadRequestException(Error error, String message) {
    super(error, message);
  }
}
