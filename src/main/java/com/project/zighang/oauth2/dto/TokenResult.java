package com.project.zighang.oauth2.dto;

public record TokenResult(TokenDto tokenDto, String userName) {
    public static TokenResult from(final TokenDto tokenDto, String userName){
        return new TokenResult(tokenDto, userName);
    }
}
