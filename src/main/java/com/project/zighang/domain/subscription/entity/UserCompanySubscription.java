package com.project.zighang.domain.subscription.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "user_company_subscription",
        uniqueConstraints = @UniqueConstraint(name="uk_company_subscription", columnNames={"user_id","company_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCompanySubscription extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    public static UserCompanySubscription create(Long userId, Long companyId) {
        return new UserCompanySubscription(userId, companyId);
    }
}
