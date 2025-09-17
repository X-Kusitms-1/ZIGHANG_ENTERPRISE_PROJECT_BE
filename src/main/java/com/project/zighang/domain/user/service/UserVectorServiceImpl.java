package com.project.zighang.domain.user.service;

import com.project.zighang.domain.post.entity.PostApplyEntity;
import com.project.zighang.domain.post.entity.ResumeFileEntity;
import com.project.zighang.domain.post.repository.PostApplyEntityRepository;
import com.project.zighang.domain.post.repository.ResumeFileEntityRepository;
import com.project.zighang.domain.user.entity.Accuracy;
import com.project.zighang.domain.user.entity.UserEntity;
import com.project.zighang.domain.user.repository.*;
import com.project.zighang.global.client.azure.ReportGenerator;
import com.project.zighang.global.client.clova.embedding.EmbeddingGenerator;
import com.project.zighang.global.client.clova.ocr.OcrReader;
import com.project.zighang.global.prompt.service.PromptBuilder;
import com.project.zighang.global.prompt.service.PromptFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserVectorServiceImpl implements UserVectorService {

    private static final Pattern CLEANUP_PATTERN = Pattern.compile("[()\\[\\]{}\"'.,;:!?\\n\\r\\t]+");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private static final String CORE_INFO_PREFIX = "핵심역량: ";
    private static final String PREFERENCE_PREFIX = "희망조건: ";
    private static final String EXPERIENCE_PREFIX = "이력서: ";
    private static final String QUALIFICATION_PREFIX = "보유자격: ";

    private final UserOnboardingRepository userOnboardingRepository;
    private final IndustryRepository industryRepository;
    private final AddressRepository addressRepository;
    private final ResumeFileEntityRepository resumeFileEntityRepository;
    private final PostApplyEntityRepository postApplyEntityRepository;
    private final AccuracyRepository accuracyRepository;

    private final EmbeddingGenerator embeddingGenerator;
    private final OcrReader ocrReader;
    private final PromptBuilder promptBuilder;
    private final ReportGenerator reportGenerator;
    private final PromptFinder promptFinder;

    @Override
    public List<Double> createUserProfileEmbedding(UserEntity user) {
        log.info("사용자 프로필 임베딩 생성 시작: userId={}", user.getId());

        try {
            String optimizedProfile = buildOptimizedUserProfile(user);
            log.info("사용자 프로필 종합 데이터: {}", optimizedProfile);

            if (!StringUtils.hasText(optimizedProfile)) {
                log.warn("빈 프로필로 인한 임베딩 생성 스킵: userId={}", user.getId());
                return null;
            }

            List<Double> embeddingVector = embeddingGenerator.embed(optimizedProfile);
            log.info("임베딩 생성 완료: userId={}, profileLength={}, embeddingSize={}",
                    user.getId(), optimizedProfile.length(), embeddingVector.size());

            return embeddingVector;

        } catch (Exception e) {
            log.error("임베딩 생성 실패: userId={}", user.getId(), e);
            throw new RuntimeException("임베딩 생성 실패", e);
        }
    }

    private String buildOptimizedUserProfile(UserEntity user) {
        ProfileBuilder builder = new ProfileBuilder();

        String coreCapabilities = buildCoreCapabilities(user);
        builder.addSection(CORE_INFO_PREFIX, coreCapabilities);

        String preferences = buildStructuredPreferences(user);
        builder.addSection(PREFERENCE_PREFIX, preferences);

        String qualifications = buildQualifications(user);
        builder.addSection(QUALIFICATION_PREFIX, qualifications);

        String experienceInResumeFiles = buildExperienceInResumeFiles(user);
        String llmResponseByResumeFiles = generateLLMResponseUsingResumeOcrData(experienceInResumeFiles);
        builder.addSection(EXPERIENCE_PREFIX, llmResponseByResumeFiles);

        return builder.build();
    }

    private String buildCoreCapabilities(UserEntity user) {
        List<String> capabilities = new ArrayList<>();

        Optional.ofNullable(findUserIndustry(user))
                .filter(StringUtils::hasText)
                .filter(industry -> !industry.contains("없습니다"))
                .ifPresent(capabilities::add);

        Optional.ofNullable(findUserCareerYear(user))
                .filter(StringUtils::hasText)
                .map(career -> "0".equals(career) ? "신입" : career + "년차")
                .ifPresent(capabilities::add);

        return String.join(" ", capabilities);
    }

    private String buildStructuredPreferences(UserEntity user) {
        List<String> preferences = new ArrayList<>();

        Optional.ofNullable(findUserPreferredLocations(user))
                .filter(StringUtils::hasText)
                .filter(location -> !location.contains("없습니다"))
                .ifPresent(location -> preferences.add(location + "근무"));

        String accuracyPreferences = buildAccuracyBasedPreferences(user);
        if (StringUtils.hasText(accuracyPreferences)) {
            preferences.add(accuracyPreferences);
        }

        return String.join(" ", preferences);
    }

    private String buildAccuracyBasedPreferences(UserEntity user) {
        Optional<Accuracy> accuracyOpt = accuracyRepository.findByUserEntityWithQuestions(user);
        if (accuracyOpt.isEmpty()) {
            return "";
        }

        Accuracy accuracy = accuracyOpt.get();
        Set<String> allPreferences = new LinkedHashSet<>();

        collectKeywords(allPreferences, accuracy.getQuestion1());
        collectKeywords(allPreferences, accuracy.getQuestion7());
        collectKeywords(allPreferences, accuracy.getQuestion8());
        collectKeywords(allPreferences, accuracy.getQuestion9());
        collectKeywords(allPreferences, accuracy.getQuestion10());

        return String.join(" ", allPreferences);
    }

    private String buildQualifications(UserEntity user) {
        Optional<Accuracy> accuracyOpt = accuracyRepository.findByUserEntityWithQuestions(user);
        if (accuracyOpt.isEmpty()) {
            return "";
        }

        Accuracy accuracy = accuracyOpt.get();
        Set<String> qualifications = new LinkedHashSet<>();

        collectKeywords(qualifications, accuracy.getQuestion2());
        collectKeywords(qualifications, accuracy.getQuestion3());
        collectKeywords(qualifications, accuracy.getQuestion4());
        collectKeywords(qualifications, accuracy.getQuestion6());

        return String.join(" ", qualifications);
    }

    private String buildExperienceInResumeFiles(UserEntity user) {
        try {
            List<ResumeFileEntity> resumeFiles = findResumeFileEntityList(user);
            if (resumeFiles.isEmpty()) {
                return "";
            }

            Set<String> experienceKeywords = new LinkedHashSet<>();

            for (ResumeFileEntity resumeFile : resumeFiles) {
                try {
                    String ocrText = ocrReader.extractTextFromPdf(resumeFile.getObjectUrl());
                    String keywords = extractResumeKeywords(ocrText);
                    if (StringUtils.hasText(keywords)) {
                        experienceKeywords.addAll(Arrays.asList(keywords.split("\\s+")));
                    }
                } catch (Exception e) {
                    log.warn("이력서 OCR 실패: resumeId={}", resumeFile.getId(), e);
                }
            }

            return String.join(" ", experienceKeywords);

        } catch (Exception e) {
            log.error("경력 키워드 추출 실패: userId={}", user.getId(), e);
            return "";
        }
    }

    private String extractResumeKeywords(String resumeText) {
        if (!StringUtils.hasText(resumeText)) {
            return "";
        }

        return CLEANUP_PATTERN.matcher(resumeText)
                .replaceAll(" ")
                .replaceAll(WHITESPACE_PATTERN.pattern(), " ")
                .trim();
    }

    private void collectKeywords(Set<String> target, Set<String> source) {
        if (source == null || source.isEmpty()) {
            return;
        }

        source.stream()
                .filter(StringUtils::hasText)
                .map(this::normalizeKeyword)
                .filter(StringUtils::hasText)
                .forEach(target::add);
    }

    private String normalizeKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return "";
        }

        return CLEANUP_PATTERN.matcher(keyword.trim())
                .replaceAll("")
                .replaceAll(WHITESPACE_PATTERN.pattern(), " ")
                .trim();
    }

    private String generateLLMResponseUsingResumeOcrData(String resumeOcrData) {
        if (!StringUtils.hasText(resumeOcrData)) {
            log.warn("Empty OCR data provided for LLM analysis");
            return "";
        }

        try {
            String prompt = promptBuilder.buildResumeAnalysisPrompt(resumeOcrData);
            String systemPrompt = promptFinder.findPromptByTag("resume_system_prompt");
            String llmResponse = reportGenerator.generateReport(systemPrompt, prompt);

            log.info("LLM analysis completed for resume");
            return llmResponse;

        } catch (Exception e) {
            log.error("Failed to generate LLM response: {}", e.getMessage(), e);
            return "";
        }
    }

    @Transactional(readOnly = true)
    protected String findUserCareerYear(UserEntity user) {
        try {
            return userOnboardingRepository.findByUserEntity(user)
                    .map(onboarding -> String.valueOf(onboarding.getCareerYears()))
                    .orElse(null);
        } catch (Exception e) {
            log.warn("경력 연수 조회 실패: userId={}", user.getId(), e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    protected String findUserIndustry(UserEntity user) {
        try {
            return industryRepository.findByUserEntity(user)
                    .map(industry -> industry.getJobFamily() + " " + industry.getRole())
                    .orElse(null);
        } catch (Exception e) {
            log.warn("직군/직무 조회 실패: userId={}", user.getId(), e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    protected String findUserPreferredLocations(UserEntity user) {
        try {
            return addressRepository.findByUserEntity(user)
                    .map(address -> address.getCity() + " " + address.getDistrict())
                    .orElse(null);
        } catch (Exception e) {
            log.warn("선호 지역 조회 실패: userId={}", user.getId(), e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    protected List<ResumeFileEntity> findResumeFileEntityList(UserEntity user) {
        try {
            List<PostApplyEntity> postApplies = postApplyEntityRepository.findAllByUserEntityWithPostFetch(user);

            return postApplies.stream()
                    .flatMap(postApply -> {
                        try {
                            return resumeFileEntityRepository.findAllByUserEntityWithPostApplyFetch(postApply).stream();
                        } catch (Exception e) {
                            log.warn("이력서 파일 조회 실패: postApplyId={}", postApply.getId(), e);
                            return Stream.empty();
                        }
                    })
                    .distinct()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("이력서 파일 목록 조회 실패: userId={}", user.getId(), e);
            return List.of();
        }
    }

    private static class ProfileBuilder {
        private final Map<String, String> sections = new LinkedHashMap<>();

        public void addSection(String prefix, String content) {
            if (StringUtils.hasText(content)) {
                sections.put(prefix, content);
            }
        }

        public String build() {
            String fullProfile = sections.entrySet().stream()
                    .map(entry -> entry.getKey() + entry.getValue())
                    .collect(Collectors.joining(" | "));

            return fullProfile.trim();
        }
    }
}