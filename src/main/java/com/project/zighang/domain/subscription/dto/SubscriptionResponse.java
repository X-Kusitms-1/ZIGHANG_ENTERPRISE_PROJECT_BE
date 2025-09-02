package com.project.zighang.domain.subscription.dto;

import com.project.zighang.domain.subscription.entity.UserCompanySubscription;

public record SubscriptionResponse(

        Long id,

        Long userId,

        Long companyId
) {
    public static SubscriptionResponse from(UserCompanySubscription subscription) {
        return new SubscriptionResponse(subscription.getId(), subscription.getUserId(), subscription.getCompanyId());
    }
}
