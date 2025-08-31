package com.project.zighang.domain.subscription.service;

import com.project.zighang.domain.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionFinderImpl implements SubscriptionFinder {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Long> findSubscribedCompanyIds(Long userId) {
        return subscriptionRepository.findCompanyIdsByUserId(userId);
    }
}
