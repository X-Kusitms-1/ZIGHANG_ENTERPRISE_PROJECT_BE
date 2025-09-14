package com.project.zighang.domain.user.repository;

import com.project.zighang.domain.user.entity.UserTodayPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTodayPostRepository extends JpaRepository<UserTodayPostEntity, Long> {

    @Query("SELECT utp FROM UserTodayPostEntity utp " +
            "JOIN FETCH utp.postEntity " +  // N+1 방지
            "WHERE utp.userEntity.id = :userId " +
            "ORDER BY utp.createdAt DESC")  // 최신 등록 순
    List<UserTodayPostEntity> findByUserEntityIdWithPost(@Param("userId") Long userId);
}
