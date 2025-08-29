package com.project.zighang.user.repository;

import com.project.zighang.user.entity.AddressEntity;
import com.project.zighang.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

    @Modifying
    @Query("DELETE FROM AddressEntity a WHERE a.userEntity = :user")
    void deleteAllByUserEntity(@Param("user") UserEntity user);
}
