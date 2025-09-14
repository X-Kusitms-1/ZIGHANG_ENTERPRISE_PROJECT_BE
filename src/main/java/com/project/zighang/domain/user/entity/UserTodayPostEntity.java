package com.project.zighang.domain.user.entity;

import com.project.zighang.domain.post.entity.PostEntity;
import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_today_posts_entity",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_post",
                        columnNames = {"user_entity_id", "recruitment_id"}
                )
})
public class UserTodayPostEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id")
    private PostEntity postEntity;

    public static UserTodayPostEntity create(UserEntity userEntity, PostEntity postEntity) {
        return UserTodayPostEntity.builder()
                .userEntity(userEntity)
                .postEntity(postEntity)
                .build();
    }
}
