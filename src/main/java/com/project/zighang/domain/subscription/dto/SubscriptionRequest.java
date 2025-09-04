package com.project.zighang.domain.subscription.dto;

import jakarta.validation.constraints.NotNull;

public record SubscriptionRequest(
        @NotNull
        Long userId,

        @NotNull
        Long companyId
) {}
