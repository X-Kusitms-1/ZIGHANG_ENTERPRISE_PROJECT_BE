package com.project.zighang.domain.user.entity;

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
    private Long minCareer;

    private Long maxCareer;

    private Long careerYears;

    private Long dailyRecommendPostCount;

    public static UserOnboardingEntity create(UserEntity userEntity, Long minCareer, Long maxCareer) {
        return UserOnboardingEntity.builder()
                .userEntity(userEntity)
                .minCareer(minCareer)
                .maxCareer(maxCareer)
                .careerYears(Math.max(0, maxCareer - minCareer))
                .dailyRecommendPostCount(0L)
                .build();
    }

    public void updateUserCareer(Long minCareer, Long maxCareer) {
        this.minCareer = minCareer;
        this.maxCareer = maxCareer;
        updateCareerYears();
    }

    public void updateDailyRecommendPostCount(Long dailyRecommendPostCount){
        this.dailyRecommendPostCount = dailyRecommendPostCount;
    }

    private void updateCareerYears() {
        careerYears = Math.max(0, maxCareer - minCareer);
    }
}
