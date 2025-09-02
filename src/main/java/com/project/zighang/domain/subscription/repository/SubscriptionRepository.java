package com.project.zighang.domain.subscription.repository;

import com.project.zighang.domain.subscription.entity.UserCompanySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<UserCompanySubscription, Long> {

    Optional<UserCompanySubscription> findByUserIdAndCompanyId(Long userId, Long companyId);

    List<UserCompanySubscription> findByUserId(Long userId);

    @Query("select s.companyId from UserCompanySubscription s where s.userId = :userId")
    List<Long> findCompanyIdsByUserId(Long userId);
}
