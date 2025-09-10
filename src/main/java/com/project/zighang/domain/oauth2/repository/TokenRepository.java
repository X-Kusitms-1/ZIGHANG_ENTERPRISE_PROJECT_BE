package com.project.zighang.domain.oauth2.repository;

import com.project.zighang.domain.oauth2.entity.TokenEntity;
import com.project.zighang.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByUserEntity(UserEntity user);
}
