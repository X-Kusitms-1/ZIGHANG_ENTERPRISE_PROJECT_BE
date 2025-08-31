package com.project.zighang.domain.subscription.service;

import com.project.zighang.domain.subscription.entity.UserCompanySubscription;
import com.project.zighang.domain.subscription.exception.model.AlreadyExistException;
import com.project.zighang.domain.subscription.exception.model.SubscriptionInternalException;
import com.project.zighang.domain.subscription.repository.UserCompanySubscriptionRepository;
import com.project.zighang.global.exception.CustomException;
import com.project.zighang.global.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserCompanySubscriptionRepository repository;

    /**
     * 구독 등록
     * @param userId
     * @param companyId
     * @return List<UserCompanySubscription>
     */
    @Transactional
    public List<UserCompanySubscription> register(Long userId, Long companyId) {
        if (repository.findByUserIdAndCompanyId(userId, companyId).isPresent()) {
            throw new AlreadyExistException(Error.ALREADY_SUBSCRIBED_COMPANY, Error.ALREADY_SUBSCRIBED_COMPANY.getMessage());
        }

        try {
            repository.save(UserCompanySubscription.create(userId, companyId));
        } catch (Exception e) {
            throw new SubscriptionInternalException(Error.SUBSCRIPTION_INTERNAL_ERROR, Error.SUBSCRIPTION_INTERNAL_ERROR.getMessage());
        }

        return repository.findByUserId(userId);
    }

    /**
     * 구독 해지
     * @param userId
     * @param companyId
     * @return List<UserCompanySubscription>
     */
    @Transactional
    public List<UserCompanySubscription> unregister(Long userId, Long companyId) {
        repository.findByUserIdAndCompanyId(userId, companyId)
                .ifPresent(subscribedCompany -> {
                    try {
                        repository.delete(subscribedCompany);
                    } catch (Exception e) {
                        throw new CustomException(Error.SUBSCRIPTION_INTERNAL_ERROR, Error.SUBSCRIPTION_INTERNAL_ERROR.getMessage());
                    }
                });

        return repository.findByUserId(userId);
    }
}
