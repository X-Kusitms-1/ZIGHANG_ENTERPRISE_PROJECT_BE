package com.project.zighang.domain.user.repository;

import com.project.zighang.domain.user.entity.UserEntity;
import com.project.zighang.domain.user.entity.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {
    Optional<WeeklyReport> findByUserEntityAndYearAndMonthAndWeekOfMonth(
            UserEntity userEntity, Integer year, Integer month, Integer weekOfMonth);

    boolean existsByUserEntityAndYearAndMonthAndWeekOfMonth(
            UserEntity userEntity, Integer year, Integer month, Integer weekOfMonth);
}