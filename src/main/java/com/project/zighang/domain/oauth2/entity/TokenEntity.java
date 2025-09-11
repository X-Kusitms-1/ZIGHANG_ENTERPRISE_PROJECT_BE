package com.project.zighang.domain.oauth2.entity;

import com.project.zighang.global.common.BaseEntity;
import com.project.zighang.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "token_entity",
        uniqueConstraints = @UniqueConstraint(name = "uk_token_user", columnNames = "user_entity_id"),
        indexes = @Index(name = "idx_token_user", columnList = "user_entity_id")
)
public class TokenEntity extends BaseEntity {

    @Column(length = 2048, nullable = false)
    private String accessToken;

    @Column(length = 2048, nullable = false)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_entity_id", unique = true, nullable = false)
    private UserEntity userEntity;

    public static TokenEntity create(String accessToken, String refreshToken, UserEntity userEntity) {
        return TokenEntity.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userEntity(userEntity)
                .build();
    }

    public void updateTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
