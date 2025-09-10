package com.project.zighang.domain.user.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeeklyReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(nullable = false)
    private Integer weekNumber;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer weekOfMonth;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reportData;

    @Column(nullable = false)
    private Integer passedCount;

    @Column(nullable = false)
    private Integer rejectedCount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public static WeeklyReport create(UserEntity userEntity, Integer weekNumber, Integer year, Integer month,
                                      Integer weekOfMonth, String reportData, Integer passedCount, Integer rejectedCount,
                                      LocalDate startDate, LocalDate endDate) {
        return WeeklyReport.builder()
                .userEntity(userEntity)
                .weekNumber(weekNumber)
                .year(year)
                .month(month)
                .weekOfMonth(weekOfMonth)
                .reportData(reportData)
                .passedCount(passedCount)
                .rejectedCount(rejectedCount)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public String getFormattedWeek() {
        return year + "년 " + month + "월 " + weekOfMonth + "주차";
    }
}
