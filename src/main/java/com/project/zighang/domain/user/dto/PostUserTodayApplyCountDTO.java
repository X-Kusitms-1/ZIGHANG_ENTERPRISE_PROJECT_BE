package com.project.zighang.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record PostUserTodayApplyCountDTO(
        @NotNull Long applyCount
) {
}
