package com.project.zighang.domain.post.entity;

import com.project.zighang.global.common.BaseEntity;
import com.project.zighang.domain.post.enumerate.ApplyStatus;
import com.project.zighang.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@Table(name = "post_apply_entity", uniqueConstraints = {
        @UniqueConstraint(
                name = "post_apply_uk",
                columnNames = {"user_entity_id", "recruitment_id"}
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostApplyEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id")
    private PostEntity postEntity;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ApplyStatus applyStatus = ApplyStatus.PENDING;

    public static PostApplyEntity create(UserEntity userEntity, PostEntity postEntity) {
        return PostApplyEntity.builder()
                .userEntity(userEntity)
                .postEntity(postEntity)
                .build();
    }

    // 합격 처리
    public void pass() {
        this.applyStatus = ApplyStatus.PASSED;
    }

    public void reject() {
        this.applyStatus = ApplyStatus.REJECTED;
    }
}
