package com.project.zighang.user.dto;

import jakarta.validation.constraints.NotNull;

public record PostUserTodayApplyCountDTO(
        Long userId,
        @NotNull Long applyCount
) {
}
