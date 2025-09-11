package com.project.zighang.domain.subscription.controller;

import com.project.zighang.domain.subscription.dto.SubscriptionResponse;
import com.project.zighang.domain.subscription.entity.UserCompanySubscription;
import com.project.zighang.domain.subscription.service.SubscriptionService;
import com.project.zighang.domain.user.entity.UserEntity;
import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.Success;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.global.exception.template.RspTemplate;
import com.project.zighang.domain.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{companyId}")
    public RspTemplate<List<SubscriptionResponse>> register(@PathVariable Long companyId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        List<UserCompanySubscription> subscriptions = subscriptionService.register(loginUser.getId(), companyId);

        List<SubscriptionResponse> subscriptionResponse = subscriptions.stream()
                .map(sub -> new SubscriptionResponse(sub.getId(), sub.getUserId(), sub.getCompanyId()))
                .toList();

        return RspTemplate.success(Success.SUBSCRIBE_SUCCESS, subscriptionResponse);
    }

    @DeleteMapping("/{companyId}")
    public RspTemplate<List<SubscriptionResponse>> unregister(@PathVariable Long companyId,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        List<UserCompanySubscription> subscriptions = subscriptionService.unregister(loginUser.getId(), companyId);

        List<SubscriptionResponse> subscriptionResponse = subscriptions.stream()
                .map(sub -> new SubscriptionResponse(sub.getId(), sub.getUserId(), sub.getCompanyId()))
                .toList();

        return RspTemplate.success(Success.UNSUBSCRIBE_SUCCESS, subscriptionResponse);
    }

    private UserEntity getUserEntityFromUserDetailsImpl(UserDetailsImpl userDetails) throws RuntimeException {
        if (userDetails == null) {
            log.warn("Authentication failed: UserDetailsImpl object is null.");
            throw new NotFoundException(com.project.zighang.global.exception.Error.NOT_FOUND_USER, Error.NOT_FOUND_USER.getMessage());
        }
        return userDetails.getUserEntity();
    }
}