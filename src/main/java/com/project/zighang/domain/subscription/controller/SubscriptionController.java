package com.project.zighang.domain.subscription.controller;

import com.project.zighang.domain.subscription.dto.SubscriptionRequest;
import com.project.zighang.domain.subscription.dto.SubscriptionResponse;
import com.project.zighang.domain.subscription.entity.UserCompanySubscription;
import com.project.zighang.domain.subscription.service.SubscriptionService;
import com.project.zighang.global.exception.Success;
import com.project.zighang.global.template.RspTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public RspTemplate<List<SubscriptionResponse>> register(@Valid @RequestBody SubscriptionRequest request) {
        List<UserCompanySubscription> subscriptions = subscriptionService.register(request.userId(), request.companyId());

        List<SubscriptionResponse> subscriptionResponse = subscriptions.stream()
                .map(sub -> new SubscriptionResponse(sub.getId(), sub.getUserId(), sub.getCompanyId()))
                .toList();

        return RspTemplate.success(Success.SUBSCRIBE_SUCCESS, subscriptionResponse);
    }

    @DeleteMapping
    public RspTemplate<List<SubscriptionResponse>> unregister(@Valid @RequestBody SubscriptionRequest request) {
        List<UserCompanySubscription> subscriptions = subscriptionService.unregister(request.userId(), request.companyId());

        List<SubscriptionResponse> subscriptionResponse = subscriptions.stream()
                .map(sub -> new SubscriptionResponse(sub.getId(), sub.getUserId(), sub.getCompanyId()))
                .toList();

        return RspTemplate.success(Success.UNSUBSCRIBE_SUCCESS, subscriptionResponse);
    }
}