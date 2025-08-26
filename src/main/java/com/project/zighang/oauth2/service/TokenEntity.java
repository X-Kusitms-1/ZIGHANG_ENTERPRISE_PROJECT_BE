package com.project.zighang.oauth2.service;

import com.project.zighang.global.common.BaseEntity;
import com.project.zighang.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "token_entity")
public class TokenEntity extends BaseEntity {

    private String accessToken;

    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    public static TokenEntity create(String accessToken, String refreshToken, UserEntity userEntity) {
        return TokenEntity.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userEntity(userEntity)
                .build();
    }
}
