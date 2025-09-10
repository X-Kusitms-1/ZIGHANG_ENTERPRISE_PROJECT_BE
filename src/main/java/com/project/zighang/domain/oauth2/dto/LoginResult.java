package com.project.zighang.domain.oauth2.dto;

import com.project.zighang.domain.user.entity.UserEntity;

public record LoginResult(UserEntity user, boolean isNewUser) {
    public static LoginResult from(final UserEntity user, boolean isNewUser){
        return new LoginResult(user, isNewUser);
    }
}
