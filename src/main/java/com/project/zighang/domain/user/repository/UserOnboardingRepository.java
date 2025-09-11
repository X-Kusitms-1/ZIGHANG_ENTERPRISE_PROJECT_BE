package com.project.zighang.domain.user.repository;

import com.project.zighang.domain.user.entity.UserEntity;
import com.project.zighang.domain.user.entity.UserOnboardingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOnboardingRepository extends JpaRepository<UserOnboardingEntity, Long> {
    Optional<UserOnboardingEntity> findByUserEntity(UserEntity userEntity);
    boolean existsByUserEntity(UserEntity userEntity);
}
