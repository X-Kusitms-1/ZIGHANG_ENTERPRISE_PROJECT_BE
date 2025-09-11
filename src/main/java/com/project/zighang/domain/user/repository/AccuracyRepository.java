package com.project.zighang.domain.user.repository;

import com.project.zighang.domain.user.entity.Accuracy;
import com.project.zighang.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccuracyRepository extends JpaRepository<Accuracy, Long> {
    Optional<Accuracy> findByUserEntity(UserEntity userEntity);
}
