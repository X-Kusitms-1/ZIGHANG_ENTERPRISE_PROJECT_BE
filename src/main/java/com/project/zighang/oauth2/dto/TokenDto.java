package com.project.zighang.oauth2.dto;

import lombok.Builder;

@Builder
public record TokenDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
    public static TokenDto of(String accessToken, String refreshToken) {
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}