package com.project.zighang.oauth2.dto;

import com.project.zighang.user.entity.UserEntity;

public record LoginResult(UserEntity user, boolean isNewUser) {
    public static LoginResult from(final UserEntity user, boolean isNewUser){
        return new LoginResult(user, isNewUser);
    }
}
