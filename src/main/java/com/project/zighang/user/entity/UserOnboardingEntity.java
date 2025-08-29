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

    // 신입 -1, 경력은 +1 +2 .. 로 받을 예정
    private Long career;

    private Long dailyRecommendPostCount;

    public static UserOnboardingEntity create(UserEntity userEntity, Long career) {
        return UserOnboardingEntity.builder()
                .userEntity(userEntity)
                .career(career)
                .dailyRecommendPostCount(0L)
                .build();
    }

    public void updateUserCareer(Long career) {
        this.career = career;
    }

    public void updateDailyRecommendPostCount(Long dailyRecommendPostCount){
        this.dailyRecommendPostCount = dailyRecommendPostCount;
    }
}
