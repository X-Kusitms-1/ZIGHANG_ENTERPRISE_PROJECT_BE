package com.project.zighang.domain.company.service;

import com.project.zighang.domain.company.dto.*;
import com.project.zighang.domain.company.entity.Company;
import com.project.zighang.domain.company.enumerate.CompanyType;
import com.project.zighang.domain.company.enumerate.JobGroup;
import com.project.zighang.domain.company.repository.CompanyNewsRepository;
import com.project.zighang.domain.company.repository.CompanyRepository;
import com.project.zighang.domain.subscription.service.SubscriptionFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQueryService {

    private final CompanyRepository companyRepository;
    private final CompanyNewsRepository companyNewsRepository;
    private final SubscriptionFinder subscriptionFinder;

    public List<CompanyWithNewsResponse> getSubscribedCompaniesWithNews(Long userId) {
        List<Long> companyIds = subscriptionFinder.findSubscribedCompanyIds(userId);
        if (companyIds.isEmpty()) {
            return List.of();
        }

        List<Company> companies = companyRepository.findAllById(companyIds);
        if (companies.isEmpty()) {
            return List.of();
        }

        // 회사별 최신 3개 뉴스만 SELECT
        List<CompanyNewsRepository.NewsSliceRow> rows = companyNewsRepository.findTopNewsByCompanyIds(companyIds, 3);

        Map<Long, List<CompanyNews>> newsMap = rows.stream()
                .collect(Collectors.groupingBy(
                        CompanyNewsRepository.NewsSliceRow::getCompanyId,
                        LinkedHashMap::new,
                        Collectors.mapping(r -> new CompanyNews(
                                r.getTitle(), r.getUrl(), r.getPublishedAt(), r.getThumbnailUrl()
                        ), Collectors.toList())
                ));

        // 입력 순서(companyIds) 보존
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < companyIds.size(); i++) order.put(companyIds.get(i), i);
        companies.sort(Comparator.comparingInt(c -> order.getOrDefault(c.getId(), Integer.MAX_VALUE)));

        return companies.stream()
                .map(c -> new CompanyWithNewsResponse(
                        toCompanyThumb(c),
                        newsMap.getOrDefault(c.getId(), List.of())
                ))
                .toList();
    }

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

        // 회사별 최신 3개 뉴스만 SELECT
        var rows = companyNewsRepository.findTopNewsByCompanyIds(ids, 3);

        Map<Long, List<CompanyNews>> newsMap = rows.stream()
                .collect(Collectors.groupingBy(
                        CompanyNewsRepository.NewsSliceRow::getCompanyId,
                        LinkedHashMap::new,
                        Collectors.mapping(r -> new CompanyNews(
                                r.getTitle(), r.getUrl(), r.getPublishedAt(), r.getThumbnailUrl()
                        ), Collectors.toList())
                ));

        List<CompanyWithNewsResponse> content = companies.stream()
                .map(c -> new CompanyWithNewsResponse(
                        toCompanyThumb(c),
                        newsMap.getOrDefault(c.getId(), List.of())
                ))
                .toList();

        return new PageImpl<>(content, pageable, companyPage.getTotalElements());
    }

    /** 상세: 회사 전체 뉴스 + 같은 타입 랜덤 3사(각 3뉴스) */
    public CompanyDetailWithSimilarResponse getDetailWithNewsAndSimilar(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NoSuchElementException("Company not found: " + companyId));

        // 회사 요약
        var companyDto = toCompanyThumb(company);

        // 전체 뉴스 (최신순)
        var newsEntities = companyNewsRepository.findAllByCompanyIdOrderByPublishedDesc(companyId);
        List<CompanyNews> newsAll = newsEntities.stream()
                .map(n -> new CompanyNews(n.getTitle(), n.getUrl(), n.getPublishedAt(), n.getThumbnailUrl()))
                .toList();

        // 유사기업: 동일 CompanyType 랜덤 3개
        List<CompanyWithNewsResponse> similar = List.of();
        if (company.getCompanyType() != null) {
            List<Long> randIds = companyRepository.findRandomIdsByType(company.getCompanyType().name(), company.getId(), 3);
            if (!randIds.isEmpty()) {
                List<Company> similars = companyRepository.findAllById(randIds);

                // 각 회사 최신 3개 뉴스
                var rows = companyNewsRepository.findTopNewsByCompanyIds(randIds, 3);
                Map<Long, List<CompanyNews>> newsMap = rows.stream()
                        .collect(Collectors.groupingBy(
                                CompanyNewsRepository.NewsSliceRow::getCompanyId,
                                LinkedHashMap::new,
                                Collectors.mapping(r -> new CompanyNews(
                                        r.getTitle(), r.getUrl(), r.getPublishedAt(), r.getThumbnailUrl()
                                ), Collectors.toList())
                        ));

                // 입력 순서(randIds) 보존
                Map<Long, Integer> order = new HashMap<>();
                for (int i = 0; i < randIds.size(); i++) order.put(randIds.get(i), i);
                similars.sort(Comparator.comparingInt(c -> order.getOrDefault(c.getId(), Integer.MAX_VALUE)));

                similar = similars.stream()
                        .map(s -> new CompanyWithNewsResponse(
                                toCompanyThumb(s),
                                newsMap.getOrDefault(s.getId(), List.of())
                        ))
                        .toList();
            }
        }

        return new CompanyDetailWithSimilarResponse(companyDto, newsAll, similar);
    }

    private CompanyThumbnail toCompanyThumb(Company c) {
        return new CompanyThumbnail(
                c.getId(),
                c.getCompanyNameKr(),
                c.getCompanyThumbnailUrl(),
                c.getCompanyType() != null ? c.getCompanyType().getDescription() : null
        );
    }

    private static <T> Set<T> normalize(Set<T> s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}
