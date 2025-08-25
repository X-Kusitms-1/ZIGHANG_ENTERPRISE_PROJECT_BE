package com.project.zighang.global.exception.model;

import com.project.zighang.global.exception.CustomException;
import com.project.zighang.global.exception.Error;

public class NotFoundException extends CustomException {
  public NotFoundException(Error error, String message) {
    super(error, message);
  }
}

