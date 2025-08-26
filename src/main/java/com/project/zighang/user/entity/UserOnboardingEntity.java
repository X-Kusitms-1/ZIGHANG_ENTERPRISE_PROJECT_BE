package com.project.zighang.user.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_onboarding_entity")
public class UserOnboardingEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    private Long career;

    private String address;

    @Enumerated(EnumType.STRING)
    private Industry industry;

    private Long dailyRecommendPostCount;

    public static UserOnboardingEntity create(UserEntity userEntity, Long career, String address, Industry industry) {
        return UserOnboardingEntity.builder()
                .userEntity(userEntity)
                .career(career)
                .address(address)
                .industry(industry)
                .dailyRecommendPostCount(0L)
                .build();
    }

    public void updateInfo(Long career, String address, Industry industry) {
        this.career = career;
        this.address = address;
        this.industry = industry;
    }

    public void updateDailyRecommendPostCount(Long dailyRecommendPostCount){
        this.dailyRecommendPostCount = dailyRecommendPostCount;
    }
}
