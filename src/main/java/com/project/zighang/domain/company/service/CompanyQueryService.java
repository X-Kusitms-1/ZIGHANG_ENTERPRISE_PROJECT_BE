package com.project.zighang.domain.company.service;

import com.project.zighang.domain.company.dto.*;
import com.project.zighang.domain.company.entity.Company;
import com.project.zighang.domain.company.enumerate.CompanyType;
import com.project.zighang.domain.company.enumerate.JobGroup;
import com.project.zighang.domain.company.repository.CompanyNewsRepository;
import com.project.zighang.domain.company.repository.CompanyRepository;
import com.project.zighang.domain.subscription.service.SubscriptionFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQueryService {

    private final CompanyRepository companyRepository;
    private final CompanyNewsRepository companyNewsRepository;
    private final SubscriptionFinder subscriptionFinder;

    public List<CompanyWithNewsResponse> getSubscribedCompaniesWithNews(Long userId) {
        log.info("구독 회사 뉴스 조회 시작 - userId: {}", userId);
        List<Long> companyIds = subscriptionFinder.findSubscribedCompanyIds(userId);
        log.debug("구독 회사 ID 조회 완료 - count: {}, companyIds: {}", companyIds.size(), companyIds);

        if (companyIds.isEmpty()) {
            log.warn("구독한 회사가 없음 - userId: {}", userId);
            return List.of();
        }

        List<Company> companies = companyRepository.findAllById(companyIds);
        log.debug("회사 정보 조회 완료 - found: {}, requested: {}", companies.size(), companyIds.size());

        if (companies.isEmpty()) {
            log.warn("회사 정보를 찾을 수 없음 - userId: {}, companyIds: {}", userId, companyIds);
            return List.of();
        }

        Set<Long> subscribedCompanyIds = userId != null
                ? Set.copyOf(subscriptionFinder.findSubscribedCompanyIds(userId))
                : Set.of();
        log.debug("구독 회사 ID Set 생성 완료 - count: {}", subscribedCompanyIds.size());

        // 회사별 최신 3개 뉴스만 SELECT
        List<CompanyNewsRepository.NewsSliceRow> rows = companyNewsRepository.findTopNewsByCompanyIds(companyIds, 3);
        log.debug("회사 뉴스 조회 완료 - total news count: {}", rows.size());

        Map<Long, List<CompanyNews>> newsMap = rows.stream()
                .collect(Collectors.groupingBy(
                        CompanyNewsRepository.NewsSliceRow::getCompanyId,
                        LinkedHashMap::new,
                        Collectors.mapping(r -> new CompanyNews(
                                r.getTitle(), r.getUrl(), r.getPublishedAt(), r.getThumbnailUrl()
                        ), Collectors.toList())
                ));
        log.debug("뉴스 데이터 그룹핑 완료 - companies with news: {}", newsMap.size());

        // 입력 순서(companyIds) 보존
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < companyIds.size(); i++) order.put(companyIds.get(i), i);
        companies.sort(Comparator.comparingInt(c -> order.getOrDefault(c.getId(), Integer.MAX_VALUE)));
        log.debug("회사 순서 정렬 완료");

        List<CompanyWithNewsResponse> result = companies.stream()
                .map(c -> new CompanyWithNewsResponse(
                        toCompanyThumb(c, subscribedCompanyIds.contains(c.getId())),
                        newsMap.getOrDefault(c.getId(), List.of())
                ))
                .toList();

        log.info("구독 회사 뉴스 조회 완료 - userId: {}, result count: {}", userId, result.size());
        return result;
    }

    /** 필터 유지 + 회사별 최신 뉴스 3개 */
    public Page<CompanyWithNewsResponse> searchWithNews(
            Set<CompanyType> types,
            Set<JobGroup> jobGroups,
            Set<String> regionCodes,
            Pageable pageable,
            Long userId
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

        // 회원인 경우에만 구독 회사 목록 조회, 비회원은 빈 Set
        Set<Long> subscribedCompanyIds = userId != null
                ? Set.copyOf(subscriptionFinder.findSubscribedCompanyIds(userId))
                : Set.of();

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
                        toCompanyThumb(c, subscribedCompanyIds.contains(c.getId())),
                        newsMap.getOrDefault(c.getId(), List.of())
                ))
                .toList();

        return new PageImpl<>(content, pageable, companyPage.getTotalElements());
    }

    /** 상세: 회사 전체 뉴스 + 같은 타입 랜덤 3사(각 3뉴스) */
    public CompanyDetailWithSimilarResponse getDetailWithNewsAndSimilar(Long companyId, Long userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NoSuchElementException("Company not found: " + companyId));

        Set<Long> subscribedCompanyIds = userId != null
                ? Set.copyOf(subscriptionFinder.findSubscribedCompanyIds(userId))
                : Set.of();

        // 회사 요약
        var companyDto = toCompanyThumb(company, subscribedCompanyIds.contains(companyId));

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
                                toCompanyThumb(s, subscribedCompanyIds.contains(s.getId())),
                                newsMap.getOrDefault(s.getId(), List.of())
                        ))
                        .toList();
            }
        }

        return new CompanyDetailWithSimilarResponse(companyDto, newsAll, similar);
    }

    private CompanyThumbnail toCompanyThumb(Company c, boolean isSubscribed) {
        return new CompanyThumbnail(
                c.getId(),
                c.getCompanyNameKr(),
                c.getCompanyThumbnailUrl(),
                c.getCompanyType() != null ? c.getCompanyType().getDescription() : null,
                isSubscribed
        );
    }

    private static <T> Set<T> normalize(Set<T> s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}
