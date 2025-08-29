package com.project.zighang.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.util.List;

public record PostUserOnboardingDto(
        @Schema(description = "사용자 ID (토큰 구현 전까지 임시 사용)", example = "1")
        Long userId,

        @Schema(description = "경력 연차 (신입: 0, 1년차: 1, 2년차: 2...)", example = "3") @Min(0)
        Long career,

        @Schema(description = "관심 지역 목록")
        List<AddressDto> addressList,

        @Schema(description = "관심 산업/직무 목록")
        List<IndustryDto> industryList
) {}