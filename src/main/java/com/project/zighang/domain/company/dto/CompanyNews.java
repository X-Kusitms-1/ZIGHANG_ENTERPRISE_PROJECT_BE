package com.project.zighang.domain.company.dto;

import java.time.LocalDate;

public record CompanyNews(
        String title,

        String url,

        LocalDate publishedAt,

        String thumbnailUrl
) {}