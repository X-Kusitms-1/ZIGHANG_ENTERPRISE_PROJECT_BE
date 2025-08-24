package com.project.zighang.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String email;

    private String name;

    private Instant createdAt;

    private Long dailyRecommendPostCount;

    public static UserEntity create(String email, String name, Long dailyRecommendPostCount) {
        return UserEntity.builder()
                .email(email)
                .name(name)
                .createdAt(Instant.now())
                .dailyRecommendPostCount(dailyRecommendPostCount)
                .build();
    }
}
