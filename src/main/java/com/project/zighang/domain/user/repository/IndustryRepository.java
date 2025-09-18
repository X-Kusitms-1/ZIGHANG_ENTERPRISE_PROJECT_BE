package com.project.zighang.domain.user.repository;

import com.project.zighang.domain.user.entity.IndustryEntity;
import com.project.zighang.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IndustryRepository extends JpaRepository<IndustryEntity, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM IndustryEntity i WHERE i.userEntity = :user")
    void deleteAllByUserEntity(@Param("user") UserEntity user);

    Optional<IndustryEntity> findByUserEntity(UserEntity user);
}
