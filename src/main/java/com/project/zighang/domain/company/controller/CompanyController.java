package com.project.zighang.domain.company.controller;

import com.project.zighang.domain.company.dto.CompanyWithNewsResponse;
import com.project.zighang.domain.company.enumerate.CompanyType;
import com.project.zighang.domain.company.enumerate.JobGroup;
import com.project.zighang.domain.company.service.CompanyQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyQueryService companyQueryService;

    // 예:
    // /api/companies/search-with-news?types=LARGE_ENTERPRISE&jobGroups=DATA_AI&regionCodes=SEOUL&page=0&size=20
    @GetMapping("/search-with-news")
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
}