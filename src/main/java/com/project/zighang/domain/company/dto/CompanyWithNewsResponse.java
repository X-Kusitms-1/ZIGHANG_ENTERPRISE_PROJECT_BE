package com.project.zighang.domain.company.dto;

import java.util.List;

public record CompanyWithNewsResponse(
        CompanyThumbnailResponse company,

        List<CompanyNewsResponse> news
) {}