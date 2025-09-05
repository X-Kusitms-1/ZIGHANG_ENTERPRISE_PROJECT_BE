package com.project.zighang.user.dto;

import jakarta.validation.constraints.NotNull;

public record PostUserTodayApplyCountDTO(
        @NotNull Long applyCount
) {
}
