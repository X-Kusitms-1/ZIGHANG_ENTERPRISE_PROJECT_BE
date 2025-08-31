package com.project.zighang.domain.subscription.repository;

import com.project.zighang.domain.subscription.entity.UserCompanySubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCompanySubscriptionRepository extends JpaRepository<UserCompanySubscription, Long> {

    Optional<UserCompanySubscription> findByUserIdAndCompanyId(Long userId, Long companyId);

    List<UserCompanySubscription> findByUserId(Long userId);
}
