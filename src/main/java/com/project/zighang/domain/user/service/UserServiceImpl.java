package com.project.zighang.domain.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.zighang.domain.oauth2.dto.TokenDto;
import com.project.zighang.domain.oauth2.service.TokenProvider;
import com.project.zighang.domain.post.dto.PostResponseDto;
import com.project.zighang.domain.post.entity.PostEntity;
import com.project.zighang.domain.post.enumerate.ApplyStatus;
import com.project.zighang.domain.post.repository.PostApplyEntityRepository;
import com.project.zighang.domain.post.repository.PostEntityRepository;
import com.project.zighang.domain.post.service.PostingFinder;
import com.project.zighang.domain.user.dto.PostUserOnboardingDto;
import com.project.zighang.domain.user.dto.PostUserTodayApplyCountDTO;
import com.project.zighang.domain.user.dto.WeekDateInfo;
import com.project.zighang.domain.user.dto.request.AccuracyRequest;
import com.project.zighang.domain.user.dto.response.AchievementResponse;
import com.project.zighang.domain.user.dto.response.ReportResponse;
import com.project.zighang.domain.user.dto.response.TodayPostResponseDto;
import com.project.zighang.domain.user.entity.*;
import com.project.zighang.domain.user.repository.*;
import com.project.zighang.global.client.azure.ReportGenerator;
import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.model.BadRequestException;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.global.prompt.service.PromptBuilder;
import com.project.zighang.global.prompt.service.PromptFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserOnboardingRepository userOnboardingRepository;
    private final AddressRepository addressRepository;
    private final IndustryRepository industryRepository;
    private final TokenProvider tokenProvider;
    private final AtomicInteger dummyUserSocialIdCounter = new AtomicInteger(-1);
    private final PromptFinder promptFinder;
    private final PromptBuilder promptBuilder;
    private final ReportGenerator reportGenerator;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final PostingFinder postingFinder;
    private final AccuracyRepository accuracyRepository;
    private final UserTodayPostRepository userTodayPostRepository;
    private final PostEntityRepository postEntityRepository;
    private final PostApplyEntityRepository postApplyEntityRepository;

    @Override
    public void addUserOnboardingInfo(PostUserOnboardingDto request, UserEntity loginUser) {
        Long minCareer = request.minCareer();
        Long maxCareer = request.maxCareer();
        if (minCareer == null || minCareer < 0 || maxCareer == null || maxCareer < 0) {
            throw new BadRequestException(Error.BAD_REQUEST_CAREER_VALUE, Error.BAD_REQUEST_CAREER_VALUE.getMessage());
        }
        if (minCareer > maxCareer) {
            throw new BadRequestException(Error.BAD_REQUEST_CAREER_VALUE, "minCareer는 maxCareer보다 클 수 없습니다.");
        }

        // Onboarding Entity
        userOnboardingRepository.findByUserEntity(loginUser)
                .ifPresentOrElse(
                        onboardingEntity -> {
                            log.error("이미 온보딩 정보가 존재합니다. userId: {}", loginUser.getId());
                        }, () -> {
                            log.info("온보딩 정보가 없어 새로 저장합니다. userId: {}", loginUser.getId());
                            UserOnboardingEntity userOnboardingEntity = UserOnboardingEntity.create(
                                    loginUser, request.minCareer(), request.maxCareer());
                            userOnboardingRepository.save(userOnboardingEntity);
                            log.info("새로운 온보딩 정보를 저장했습니다.");
                        }
                );

        // Address Entity
        addressRepository.deleteAllByUserEntity(loginUser);

        List<AddressEntity> newAddresses = request.addressList().stream()
                .map(dto -> AddressEntity.create(dto.city(), dto.district(), loginUser))
                .collect(Collectors.toList());

        addressRepository.saveAll(newAddresses);

        // Industry Entity
        industryRepository.deleteAllByUserEntity(loginUser);

        List<IndustryEntity> newIndustries = request.industryList().stream()
                .map(dto -> IndustryEntity.create(dto.jobFamily(), dto.role(), loginUser))
                .collect(Collectors.toList());

        industryRepository.saveAll(newIndustries);
    }

    @Override
    public void setUserApplyCount(PostUserTodayApplyCountDTO request, UserEntity loginUser) {
        if (request.applyCount() < 0) {
            throw  new BadRequestException(Error.BAD_REQUEST_APPLY_COUNT_VALUE, Error.BAD_REQUEST_APPLY_COUNT_VALUE.getMessage());
        }

        UserOnboardingEntity userOnboardingEntity = userOnboardingRepository.findByUserEntity(loginUser).orElseThrow(
                () -> new NotFoundException(Error.NOT_FOUND_USER_ONBOARDING, Error.NOT_FOUND_USER_ONBOARDING.getMessage())
        );

        userOnboardingEntity.updateDailyRecommendPostCount(request.applyCount());
    }

    @Override
    public boolean isUserOnboarded(UserEntity loginUser) {
        return userOnboardingRepository.existsByUserEntity(loginUser);
    }

    @Override
    public ReportResponse.ReportDataDto generateUserReport(UserEntity user) throws Exception {
        List<PostEntity> rejectedPosts = postingFinder.findPostingsByStatus(user, ApplyStatus.REJECTED);
        List<PostEntity> passedPosts = postingFinder.findPostingsByStatus(user, ApplyStatus.PASSED);

        if (passedPosts.isEmpty() && rejectedPosts.isEmpty()) {
            throw new BadRequestException(Error.NO_DATA_AT_WEEKLY_REPORT, "해당 주차에 합격/불합격 데이터가 없습니다.");}

        String systemPrompt = promptFinder.findPromptByTag("report_new");
        String userPrompt = promptBuilder.buildPromptFromPosts(passedPosts, rejectedPosts);
        log.info("요청 프롬프트 생성 완료");
        log.info("응답 생성 요청 중");

        String raw = reportGenerator.generateReport(systemPrompt, userPrompt);
        log.info("응답 생성 완료");

        JsonNode jsonNode = objectMapper.readTree(raw);
        log.info("json response: {}", jsonNode);
        return objectMapper.treeToValue(jsonNode, ReportResponse.ReportDataDto.class);
    }

    @Override
    public TokenDto saveDummyUser() {
        log.info("새로운 더미 사용자 생성 및 토큰 발급 시작");

        UserEntity newDummyUser = createNewDummyUser();
        TokenDto token = tokenProvider.createToken(newDummyUser);

        log.info("생성된 AccessToken: {}", token.accessToken() + "...");
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodayPostResponseDto> getUserTodayPosts(UserEntity loginUser) {
        List<UserTodayPostEntity> entities = getUserTodayPostsSafely(loginUser.getId());

        List<Long> recruitmentIds = entities.stream()
                .map(entity -> entity.getPostEntity().getRecruitmentId())
                .toList();

        List<PostEntity> posts = postEntityRepository.findAllById(recruitmentIds);

        Map<Long, Boolean> applyStatusMap = new HashMap<>();
        for (PostEntity post : posts) {
            Boolean isApplied = postApplyEntityRepository.existsByUserEntityAndPostEntity(loginUser, post);
            applyStatusMap.put(post.getRecruitmentId(), isApplied);
        }

        return posts.stream()
                .map(post -> TodayPostResponseDto.from(post, applyStatusMap.get(post.getRecruitmentId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserTodayPostEntity> getUserTodayPostsSafely(Long userId) {
        try {
            List<UserTodayPostEntity> result = userTodayPostRepository.findByUserEntityIdWithPost(userId);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            log.warn("사용자 {}의 오늘 게시글 조회 실패: {}", userId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteUserTodayPosts(UserEntity loginUser) {
        userTodayPostRepository.deleteByUserEntity(loginUser);
    }

    @Override
    public AchievementResponse getAchievementStatus(UserEntity loginUser) {
        Long dailyRecommendPostCount = userOnboardingRepository.findByUserEntity(loginUser)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_ONBOARDING, Error.NOT_FOUND_USER_ONBOARDING.getMessage()))
                .getDailyRecommendPostCount();

        Integer todayApplyCount = getTodayApplyCount(loginUser.getId());

        return AchievementResponse.of(todayApplyCount, dailyRecommendPostCount);
    }

    private Integer getTodayApplyCount(Long userId) {
        LocalDateTime[] todayRange = getTodayRange();
        return postApplyEntityRepository.getTodayApplyCount(userId, todayRange[0], todayRange[1]);
    }

    private LocalDateTime[] getTodayRange() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return new LocalDateTime[]{startOfDay, endOfDay};
    }

    private UserEntity createNewDummyUser() {
        long socialId = dummyUserSocialIdCounter.getAndDecrement();

        UserEntity dummyUser = UserEntity.builder()
                .email("dummy_" + Math.abs(socialId) + "@test.com")
                .name("더미사용자_" + Math.abs(socialId))
                .provider("dummy_user")
                .socialId(socialId)
                .build();

        return userRepository.save(dummyUser);
    }

    @Override
    public ReportResponse.Weekly generateWeeklyReport(UserEntity user, Integer year, Integer month, Integer weekOfMonth) throws Exception {

        Optional<WeeklyReport> existingReport = weeklyReportRepository.findByUserEntityAndYearAndMonthAndWeekOfMonth(user, year, month, weekOfMonth);
        if (existingReport.isPresent()) {
            WeeklyReport report = existingReport.get();
            ReportResponse.ReportDataDto reportData = objectMapper.readValue(report.getReportData(), ReportResponse.ReportDataDto.class);
            return ReportResponse.Weekly.from(report, reportData);
        }

        WeekDateInfo weekDateInfo = calculateWeekDates(year, month, weekOfMonth);
        List<PostEntity> passedPosts = postingFinder.findPostingsByStatusAndDateRange(user, ApplyStatus.PASSED, weekDateInfo.startDate(), weekDateInfo.endDate());
        List<PostEntity> rejectedPosts = postingFinder.findPostingsByStatusAndDateRange(user, ApplyStatus.REJECTED, weekDateInfo.startDate(), weekDateInfo.endDate());

        if (passedPosts.isEmpty() && rejectedPosts.isEmpty()) {
            throw new BadRequestException(Error.NO_DATA_AT_WEEKLY_REPORT, "해당 주차에 합격/불합격 데이터가 없습니다.");
        }

        String systemPrompt = promptFinder.findPromptByTag("report_new");
        String userPrompt = promptBuilder.buildPromptFromPosts(passedPosts, rejectedPosts);
        String raw = reportGenerator.generateReport(systemPrompt, userPrompt);

        JsonNode jsonNode = objectMapper.readTree(raw);
        ReportResponse.ReportDataDto reportData = objectMapper.treeToValue(jsonNode, ReportResponse.ReportDataDto.class);

        String reportJson = objectMapper.writeValueAsString(reportData);
        WeeklyReport weeklyReport = WeeklyReport.create(
                user,
                weekDateInfo.weekNumber(),
                year,
                month,
                weekOfMonth,
                reportJson,
                passedPosts.size(),
                rejectedPosts.size(),
                weekDateInfo.startDate(),
                weekDateInfo.endDate()
        );

        weeklyReportRepository.save(weeklyReport);

        return ReportResponse.Weekly.from(weeklyReport, reportData);
    }

    private WeekDateInfo calculateWeekDates(Integer year, Integer month, Integer weekOfMonth) {

        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        LocalDate startDate = firstDayOfMonth.plusDays((weekOfMonth - 1) * 7L - (dayOfWeek - 1));
        if (startDate.getMonthValue() < month) {
            startDate = firstDayOfMonth;
        }

        LocalDate endDate = startDate.plusDays(6);
        if (endDate.getMonthValue() > month) {
            endDate = firstDayOfMonth.plusMonths(1).minusDays(1);
        }

        int weekNumber = startDate.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        return new WeekDateInfo(firstDayOfMonth, startDate, endDate, weekNumber);
    }

    @Override
    public boolean checkWeeklyReportExists(UserEntity user, Integer year, Integer month, Integer weekOfMonth) {
        return weeklyReportRepository.existsByUserEntityAndYearAndMonthAndWeekOfMonth(user, year, month, weekOfMonth);
    }

    @Override
    public Accuracy createAccuracy(UserEntity userEntity, AccuracyRequest.answers answers) {
        if (accuracyRepository.findByUserEntity(userEntity).isPresent()) {
            throw new BadRequestException(Error.ACCURACY_ALREADY_EXIST, "이미 사용자의 데이터가 존재합니다.");
        }

        Accuracy accuracy = Accuracy.create(userEntity, answers);
        return accuracyRepository.save(accuracy);
    }

    @Override
    public Accuracy updateAccuracy(UserEntity userEntity, AccuracyRequest.answers answers) {
        Accuracy accuracy = accuracyRepository.findByUserEntity(userEntity)
                .orElseThrow(() -> new NotFoundException(Error.ACCURACY_NOT_FOUND, "사요자의 정확도 높이기 데이터를 찾을 수 없습니다."));
        accuracy.update(answers);
        return accuracy;
    }

    @Override
    @Transactional(readOnly = true)
    public Accuracy getAccuracy(UserEntity userEntity){
        return accuracyRepository.findByUserEntityWithQuestions(userEntity)
                .orElseThrow(() -> new NotFoundException(Error.ACCURACY_NOT_FOUND, "사용자의 정확도 높이기 데이터를 찾을 수 없습니다."));
    }
}
