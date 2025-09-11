package com.project.zighang.domain.user.dto;

import java.time.LocalDate;

public record WeekDateInfo(
        LocalDate firstDayOfMonth,
        LocalDate startDate,
        LocalDate endDate,
        int weekNumber
) {}
