package com.project.zighang.domain.user.controller;

import com.project.zighang.domain.user.dto.request.ReportRequest;
import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.Success;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.global.exception.template.RspTemplate;
import com.project.zighang.domain.oauth2.dto.TokenDto;
import com.project.zighang.domain.user.dto.PostUserOnboardingDto;
import com.project.zighang.domain.user.dto.PostUserTodayApplyCountDTO;
import com.project.zighang.domain.user.dto.UserStatusDto;
import com.project.zighang.domain.user.dto.response.ReportResponse;
import com.project.zighang.domain.user.entity.UserDetailsImpl;
import com.project.zighang.domain.user.entity.UserEntity;
import com.project.zighang.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/onboarding")
    @Operation(
            summary = "사용자 온보딩 정보 저장",
            description = "사용자의 온보딩 정보(경력, 관심지역 목록, 관심산업 목록)를 저장합니다. "
                            + "기존 저장 데이터가 있으면 모두 삭제 후 새로 받은 정보로 대체합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 온보딩 POST API 호출에 성공했습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 정보입니다.")
    })
    public RspTemplate<Void> postUserOnboardingInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PostUserOnboardingDto postUserOnboardingDto
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        log.info("START postUserOnboardingInfo for user: {}", loginUser.getId());
        userService.addUserOnboardingInfo(postUserOnboardingDto, loginUser);
        log.info("SUCCESS postUserOnboardingInfo for user: {}", loginUser.getId());
        return RspTemplate.success(Success.POST_USER_Onboarding_API_REQUEST_SUCCESS);
    }

    @GetMapping("/status")
    @Operation(
            summary = "사용자 온보딩 상태 정보 조회"
    )
    public RspTemplate<?> getUserStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        boolean isUserOnboarded = userService.isUserOnboarded(loginUser);
        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, new UserStatusDto(isUserOnboarded));
    }

    @PostMapping("/today-apply")
    @Operation(
            summary = "사용자 오늘의 지원 개수 정보 저장"
    )
    public RspTemplate<?> postUserTodayApplyCount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PostUserTodayApplyCountDTO dto
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        log.info("START postUserTodayApplyCount for user: {}, count: {}", loginUser.getId(), dto.applyCount());
        userService.setUserApplyCount(dto, loginUser);
        log.info("SUCCESS postUserTodayApplyCount for user: {}", loginUser.getId());
        return RspTemplate.success(Success.POST_USER_APPLY_COUNT_API_REQUEST_SUCCESS);
    }

    private UserEntity getUserEntityFromUserDetailsImpl(UserDetailsImpl userDetails) throws RuntimeException {
        if (userDetails == null) {
            log.warn("Authentication failed: UserDetailsImpl object is null.");
            throw new NotFoundException(Error.NOT_FOUND_USER, Error.NOT_FOUND_USER.getMessage());
        }
        return userDetails.getUserEntity();
    }

    @GetMapping("/create-dummy-with-token")
    @Operation(summary = "더미 유저의 jwt 토큰 발급", description = "항상 새로운 사용자가 생성되고 jwt 토큰이 발급됩니다.")
    public RspTemplate<?> createDummyUserWithToken() {
        TokenDto token = userService.saveDummyUser();
        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, token);
    }

    @Operation(summary = "전체 지원 기반 레포트 생성", description = "합격/불합격 채용 공고 전체를 분석하여 사용자의 강약점을 도출합니다.")
    @ApiResponse(responseCode = "200", description = "분석 결과 반환", content = @Content(mediaType = "application/json"))
    @PostMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public RspTemplate<ReportResponse.ReportDataDto> analyze(@AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        ReportResponse.ReportDataDto reportData = userService.generateUserReport(userDetails.getUserEntity());
        return RspTemplate.success(Success.CREATE_REPORT_SUCCESS, reportData);
    }

    @Operation(summary = "주차별 레포트 조회 및 생성", description = "특정 년도, 월, 주차의 합격/불합격 공고를 분석한 레포트를 조회 및 분석합니다.")
    @ApiResponse(responseCode = "200", description = "분석 결과 반환", content = @Content(mediaType = "application/json"))
    @PostMapping(value = "/weekly-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public RspTemplate<ReportResponse.Weekly> getWeeklyReport(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ReportRequest.Weekly request) throws Exception {

        LocalDate now = LocalDate.now();
        Integer year = request.year();
        Integer month = request.month();
        Integer weekOfMonth = request.weekOfMonth();

        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();
        if (weekOfMonth == null) {
            LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
            int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
            weekOfMonth = (now.getDayOfMonth() + dayOfWeek - 2) / 7 + 1;
        }

        UserEntity user = getUserEntityFromUserDetailsImpl(userDetails);
        ReportResponse.Weekly report = userService.generateWeeklyReport(user, year, month, weekOfMonth);

        return RspTemplate.success(Success.CREATE_REPORT_SUCCESS, report);
    }
}
