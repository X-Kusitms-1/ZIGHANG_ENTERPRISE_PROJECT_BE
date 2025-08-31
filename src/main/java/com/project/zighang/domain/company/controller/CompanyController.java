package com.project.zighang.domain.company.controller;

import com.project.zighang.domain.company.dto.CompanyWithNewsResponse;
import com.project.zighang.domain.company.dto.CompanyDetailWithSimilarResponse;
import com.project.zighang.domain.company.enumerate.CompanyType;
import com.project.zighang.domain.company.enumerate.JobGroup;
import com.project.zighang.domain.company.service.CompanyQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyQueryService companyQueryService;

    // 예:
    // /api/companies/search-with-news?types=LARGE_ENTERPRISE&jobGroups=DATA_AI&regionCodes=SEOUL&page=0&size=20
    @GetMapping
    public Page<CompanyWithNewsResponse> searchWithNews(
            @RequestParam(required = false) Set<CompanyType> types,
            @RequestParam(required = false) Set<JobGroup> jobGroups,
            @RequestParam(required = false) Set<String> regionCodes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "companyNameKr,asc") String sort
    ) {
        String[] s = sort.split(",", 2);
        Sort springSort = Sort.by(Sort.Direction.fromString(s.length > 1 ? s[1] : "asc"),
                s[0]);
        Pageable pageable = PageRequest.of(page, size, springSort);
        return companyQueryService.searchWithNews(types, jobGroups, regionCodes, pageable);
    }

    /** 상세: 회사 전체 뉴스 + 같은 타입 랜덤 3사(각 3뉴스) */
    @GetMapping("/{companyId}")
    public CompanyDetailWithSimilarResponse getDetail(@PathVariable Long companyId) {
        return companyQueryService.getDetailWithNewsAndSimilar(companyId);
    }
}