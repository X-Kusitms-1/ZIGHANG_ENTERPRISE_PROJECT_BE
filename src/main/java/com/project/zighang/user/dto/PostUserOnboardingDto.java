package com.project.zighang.user.dto;

import com.project.zighang.user.entity.Industry;

public record PostUserOnboardingDto(
        Long userId, // token 구현 전까지는 userId로 처리
        Long career, String address, Industry industry
) {}