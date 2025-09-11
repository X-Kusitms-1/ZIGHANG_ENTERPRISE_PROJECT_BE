package com.project.zighang.domain.user.entity;

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
public class AddressEntity extends BaseEntity {

    private String city;

    private String district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    public static AddressEntity create(String city, String district, UserEntity userEntity) {
        return AddressEntity.builder()
                .city(city)
                .district(district)
                .userEntity(userEntity)
                .build();
    }
}
