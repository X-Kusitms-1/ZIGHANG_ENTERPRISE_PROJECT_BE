package com.project.zighang.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.zighang.global.client.azure.ReportGenerator;
import com.project.zighang.global.client.azure.dto.AnalysisRequest;
import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.model.BadRequestException;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.global.prompt.service.PromptBuilder;
import com.project.zighang.global.prompt.service.PromptFinder;
import com.project.zighang.oauth2.dto.TokenDto;
import com.project.zighang.oauth2.service.TokenProvider;
import com.project.zighang.user.dto.PostUserOnboardingDto;
import com.project.zighang.user.dto.PostUserTodayApplyCountDTO;
import com.project.zighang.user.dto.response.ReportResponse;
import com.project.zighang.user.entity.AddressEntity;
import com.project.zighang.user.entity.IndustryEntity;
import com.project.zighang.user.entity.UserEntity;
import com.project.zighang.user.entity.UserOnboardingEntity;
import com.project.zighang.user.repository.AddressRepository;
import com.project.zighang.user.repository.IndustryRepository;
import com.project.zighang.user.repository.UserOnboardingRepository;
import com.project.zighang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    // 1. 사용자 정보 기반으로 합격공고 조합, 실패 공고 조합
    // 2. 시스템 프롬프트, 유저 프롬프트 찾기
    // String systemPrompt = promptFinder.findPromptByTag("report");
    // String userPrompt = promptBuilder.build(req);
    // 3. openAI API 호출을 통해서 레포트 호출
    // String raw = azureReportService.chatJsonOnly(systemPrompt, userPrompt)
    // JsonNode jsonNode = objectMapper.readTree(raw);
    // 4. 레포트 저장
    // 5. 레포트 조회
    public ReportResponse.ReportDataDto generateUserReport(UserEntity user) throws Exception {
        String systemPrompt = promptFinder.findPromptByTag("report_new");
        String userPrompt = promptBuilder.buildReportRequest(user);
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
}
