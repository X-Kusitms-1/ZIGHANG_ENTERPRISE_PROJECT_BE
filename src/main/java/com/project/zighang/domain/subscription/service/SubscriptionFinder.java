package com.project.zighang.domain.subscription.service;

import java.util.List;

public interface SubscriptionFinder {

    List<Long> findSubscribedCompanyIds(Long userId);
}
