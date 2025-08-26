package com.project.zighang.oauth2.dto;

import com.project.zighang.user.entity.UserEntity;

public record TokenResult(TokenDto tokenDto, Boolean isNewUser) {
    public static TokenResult from(final TokenDto tokenDto, boolean isNewUser){
        return new TokenResult(tokenDto, isNewUser);
    }
}
