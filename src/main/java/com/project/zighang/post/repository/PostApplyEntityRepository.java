package com.project.zighang.post.repository;

import com.project.zighang.post.entity.PostApplyEntity;
import com.project.zighang.post.entity.PostEntity;
import com.project.zighang.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostApplyEntityRepository extends JpaRepository<PostApplyEntity, Long> {

    boolean existsByUserEntityAndPostEntity(UserEntity userEntity, PostEntity postEntity);

    @Query("SELECT pa FROM PostApplyEntity pa JOIN FETCH pa.postEntity WHERE pa.userEntity = :user")
    List<PostApplyEntity> findAllByUserEntityWithPostFetch(@Param("user") UserEntity user);
}
