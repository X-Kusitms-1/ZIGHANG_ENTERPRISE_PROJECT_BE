package com.project.zighang.domain.company.service;

import com.project.zighang.domain.company.dto.*;
import com.project.zighang.domain.company.entity.Company;
import com.project.zighang.domain.company.entity.CompanyNews;
import com.project.zighang.domain.company.enumerate.CompanyType;
import com.project.zighang.domain.company.enumerate.JobGroup;
import com.project.zighang.domain.company.repository.CompanyRepository;
import com.project.zighang.domain.company.repository.CompanyNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQueryService {

    private final CompanyRepository companyRepository;
    private final CompanyNewsRepository companyNewsRepository;

    /** 필터 유지 + 회사별 최신 뉴스 3개 */
    public Page<CompanyWithNewsResponse> searchWithNews(
            Set<CompanyType> types,
            Set<JobGroup> jobGroups,
            Set<String> regionCodes,
            Pageable pageable
    ) {
        types       = normalize(types);
        jobGroups   = normalize(jobGroups);
        regionCodes = normalize(regionCodes);

        Page<Company> companyPage = companyRepository.searchByFilters(types, jobGroups, regionCodes, pageable);
        if (companyPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Company> companies = companyPage.getContent();
        List<Long> ids = companies.stream().map(Company::getId).toList();

        // 회사별 최신 3개만 SELECT (네이티브)
        var rows = companyNewsRepository.findTopNewsByCompanyIds(ids, 3);

        // 회사별 -> 뉴스 DTO 목록
        Map<Long, List<CompanyNewsResponse>> newsMap = rows.stream()
                .collect(Collectors.groupingBy(
                        CompanyNewsRepository.NewsSliceRow::getCompanyId,
                        LinkedHashMap::new,
                        Collectors.mapping(r -> new CompanyNewsResponse(
                                r.getTitle(),
                                r.getUrl(),
                                r.getPublishedAt(),
                                r.getThumbnailUrl()
                        ), Collectors.toList())
                ));

        // 응답 매핑 (회사 순서 보존)
        List<CompanyWithNewsResponse> content = new ArrayList<>(companies.size());
        for (Company c : companies) {
            var companyDto = new CompanyThumbnailResponse(
                    c.getId(),
                    c.getCompanyNameKr(),
                    c.getCompanyThumbnailUrl(),
                    c.getCompanyType() != null ? c.getCompanyType().getDescription() : null
            );
            var news = newsMap.getOrDefault(c.getId(), List.of());
            content.add(new CompanyWithNewsResponse(companyDto, news));
        }

        return new PageImpl<>(content, pageable, companyPage.getTotalElements());
    }

    private static <T> Set<T> normalize(Set<T> s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}