package com.project.zighang.user.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Table(name = "user_entity")
public class UserEntity extends BaseEntity {

    private String email;

    private String name;

    private String provider;

    // 소셜 로그인으로부터 얻은 사용자 고유 ID
    private String socialId;

    public static UserEntity create(String email, String name, String provider, String socialId) {
        return UserEntity.builder()
                .email(email)
                .name(name)
                .provider(provider)
                .socialId(socialId)
                .build();
    }
}
