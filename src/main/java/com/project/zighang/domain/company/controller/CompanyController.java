package com.project.zighang.domain.company.controller;

import com.project.zighang.domain.company.dto.CompanyWithNewsResponse;
import com.project.zighang.domain.company.dto.CompanyDetailWithSimilarResponse;
import com.project.zighang.domain.company.enumerate.CompanyType;
import com.project.zighang.domain.company.enumerate.JobGroup;
import com.project.zighang.domain.company.service.CompanyQueryService;
import com.project.zighang.global.exception.Success;
import com.project.zighang.global.template.RspTemplate;
import com.project.zighang.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.List;

@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyQueryService companyQueryService;

    @GetMapping
    @Operation(summary = "기업 필터링", description = "기업을 뉴스와 함께 필터링하여 조회합니다.")
    public RspTemplate<Page<CompanyWithNewsResponse>> searchWithNews(
            @RequestParam(required = false) Set<CompanyType> types,
            @RequestParam(required = false) Set<JobGroup> jobGroups,
            @RequestParam(required = false) Set<String> regionCodes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "companyNameKr,asc") String sort,
            @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        String[] s = sort.split(",", 2);
        Sort springSort = Sort.by(Sort.Direction.fromString(s.length > 1 ? s[1] : "asc"), s[0]);
        Pageable pageable = PageRequest.of(page, size, springSort);

        Long userId = userDetails != null ? userDetails.getId() : null;

        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, companyQueryService.searchWithNews(types, jobGroups, regionCodes, pageable, userId));
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "기업뉴스 상세조회", description = "기업 뉴스와 함께, 같은 타입의 유사 기업 3곳을 랜덤으로 조회합니다.")
    public RspTemplate<CompanyDetailWithSimilarResponse> getDetail(@PathVariable Long companyId,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails != null ? userDetails.getId() : null;

        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, companyQueryService.getDetailWithNewsAndSimilar(companyId, userId));
    }

    @GetMapping("/subscriptions")
    @Operation(summary = "구독한 회사들 조회", description = "구독한 회사들의 정보와 최신 뉴스를 조회합니다.")
    public RspTemplate<List<CompanyWithNewsResponse>> getSubscribedCompaniesWithNews(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails != null ? userDetails.getId() : null;

        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, companyQueryService.getSubscribedCompaniesWithNews(userId));
    }
}