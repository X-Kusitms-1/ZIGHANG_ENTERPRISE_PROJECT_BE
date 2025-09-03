package com.project.zighang.post.repository;

import com.project.zighang.post.entity.PostApplyEntity;
import com.project.zighang.post.entity.PostEntity;
import com.project.zighang.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostApplyEntityRepository extends JpaRepository<PostApplyEntity, Long> {
    boolean existsByUserEntityAndPostEntity(UserEntity userEntity, PostEntity postEntity);
}
