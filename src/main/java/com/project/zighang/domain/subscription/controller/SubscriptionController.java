package com.project.zighang.domain.subscription.controller;

import com.project.zighang.domain.subscription.dto.SubscriptionResponse;
import com.project.zighang.domain.subscription.entity.UserCompanySubscription;
import com.project.zighang.domain.subscription.service.SubscriptionService;
import com.project.zighang.global.exception.Success;
import com.project.zighang.global.exception.template.RspTemplate;
import com.project.zighang.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{companyId}")
    public RspTemplate<List<SubscriptionResponse>> register(@PathVariable Long companyId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserCompanySubscription> subscriptions = subscriptionService.register(userDetails.getId(), companyId);

        List<SubscriptionResponse> subscriptionResponse = subscriptions.stream()
                .map(sub -> new SubscriptionResponse(sub.getId(), sub.getUserId(), sub.getCompanyId()))
                .toList();

        return RspTemplate.success(Success.SUBSCRIBE_SUCCESS, subscriptionResponse);
    }

    @DeleteMapping("/{companyId}")
    public RspTemplate<List<SubscriptionResponse>> unregister(@PathVariable Long companyId,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserCompanySubscription> subscriptions = subscriptionService.unregister(userDetails.getId(), companyId);

        List<SubscriptionResponse> subscriptionResponse = subscriptions.stream()
                .map(sub -> new SubscriptionResponse(sub.getId(), sub.getUserId(), sub.getCompanyId()))
                .toList();

        return RspTemplate.success(Success.UNSUBSCRIBE_SUCCESS, subscriptionResponse);
    }
}