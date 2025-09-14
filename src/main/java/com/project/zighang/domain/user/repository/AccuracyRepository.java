package com.project.zighang.domain.user.repository;

import com.project.zighang.domain.user.entity.Accuracy;
import com.project.zighang.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccuracyRepository extends JpaRepository<Accuracy, Long> {
    Optional<Accuracy> findByUserEntity(UserEntity userEntity);

    @Query("SELECT a FROM Accuracy a " +
            "LEFT JOIN FETCH a.question1 " +
            "LEFT JOIN FETCH a.question2 " +
            "LEFT JOIN FETCH a.question3 " +
            "LEFT JOIN FETCH a.question4 " +
            "LEFT JOIN FETCH a.question5 " +
            "LEFT JOIN FETCH a.question6 " +
            "LEFT JOIN FETCH a.question7 " +
            "LEFT JOIN FETCH a.question8 " +
            "LEFT JOIN FETCH a.question9 " +
            "LEFT JOIN FETCH a.question10 " +
            "WHERE a.userEntity = :userEntity")
    Optional<Accuracy> findByUserEntityWithQuestions(@Param("userEntity") UserEntity userEntity);
}
