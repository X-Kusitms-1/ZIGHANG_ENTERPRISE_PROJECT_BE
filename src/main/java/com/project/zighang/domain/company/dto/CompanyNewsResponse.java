package com.project.zighang.domain.company.dto;

import java.time.LocalDate;

public record CompanyNewsResponse(
        String title,

        String url,

        LocalDate publishedAt,

        String thumbnailUrl
) {}