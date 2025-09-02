package com.project.zighang.domain.company.dto;

import java.util.List;

public record CompanyWithNewsResponse(
        CompanyThumbnail company,

        List<CompanyNews> news
) {}