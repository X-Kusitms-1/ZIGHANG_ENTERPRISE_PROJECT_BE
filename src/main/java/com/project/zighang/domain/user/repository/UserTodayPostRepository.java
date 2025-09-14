package com.project.zighang.domain.user.repository;

import com.project.zighang.domain.user.entity.UserTodayPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTodayPostRepository extends JpaRepository<UserTodayPostEntity, Long> {
}
