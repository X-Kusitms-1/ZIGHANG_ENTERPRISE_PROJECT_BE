package com.project.zighang.user.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IndustryEntity extends BaseEntity {

    // 직군
    private String jobFamily;

    // 직무
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    public static IndustryEntity create(String jobFamily, String role, UserEntity userEntity) {
        return IndustryEntity.builder()
                .jobFamily(jobFamily)
                .role(role)
                .userEntity(userEntity)
                .build();
    }
}
