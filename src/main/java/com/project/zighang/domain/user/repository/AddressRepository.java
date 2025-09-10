package com.project.zighang.domain.user.repository;

import com.project.zighang.domain.user.entity.AddressEntity;
import com.project.zighang.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM AddressEntity a WHERE a.userEntity = :user")
    void deleteAllByUserEntity(@Param("user") UserEntity user);
}
