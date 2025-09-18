package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.dto.*;
import com.project.zighang.domain.post.enumerate.ApplyStatus;
import com.project.zighang.domain.user.entity.UserTodayPostEntity;
import com.project.zighang.domain.user.repository.UserTodayPostRepository;
import com.project.zighang.domain.user.service.UserVectorService;
import com.project.zighang.global.client.opensearch.PostRecommendsFinder;
import com.project.zighang.global.client.opensearch.dto.OpenSearchDto;
import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.model.BadRequestException;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.domain.post.entity.PostApplyEntity;
import com.project.zighang.domain.post.repository.PostApplyEntityRepository;
import com.project.zighang.domain.post.repository.PostEntityRepository;
import com.project.zighang.domain.post.entity.PostEntity;
import com.project.zighang.domain.user.entity.UserEntity;
import com.project.zighang.domain.user.entity.UserOnboardingEntity;
import com.project.zighang.domain.user.repository.UserOnboardingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserOnboardingRepository userOnboardingRepository;
    private final PostApplyEntityRepository postApplyEntityRepository;
    private final UserTodayPostRepository userTodayPostRepository;
    private final PostRecommendsFinder postRecommendsFinder;
    private final UserVectorService userVectorService;
    private final PostRecommendationService postRecommendationService;

    private static final int MAX_SIZE = 50;

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPostList(int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), MAX_SIZE);
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by("viewCount").descending());
        return findPostsByViewCount(pageable).map(PostResponseDto::from);
    }

    @Override
    public List<TodayApplyPostsResponseDto> getTodayApplyPostList(GetTodayApplyPostsRequest request, UserEntity loginUser) {

        UserOnboardingEntity userOnboardingEntity = userOnboardingRepository.findByUserEntity(loginUser)
                .orElseThrow(() -> new NotFoundException(
                        Error.NOT_FOUND_USER_ONBOARDING,
                        Error.NOT_FOUND_USER_ONBOARDING.getMessage())
                );

        int displayCount = (int) (userOnboardingEntity.getDailyRecommendPostCount() + 5);

        log.info("[조회수 순 조회] User:{} 요청개수:{}", loginUser.getId(), displayCount);

        List<PostEntity> topPosts = postEntityRepository.findAllByOrderByViewCountDesc(
                PageRequest.of(0, displayCount)
        );

        if (topPosts.isEmpty()) {
            log.warn("조회할 공고가 없습니다. User:{}", loginUser.getId());
            return new ArrayList<>();
        }

        return topPosts.stream()
                .map(TodayApplyPostsResponseDto::from)
                .collect(Collectors.toList());

//        if (request.isFirstApiCall()) {
//            // 사용자 온보딩 정보 조회
//            UserOnboardingEntity userOnboardingEntity = userOnboardingRepository.findByUserEntity(loginUser)
//                    .orElseThrow(() -> new NotFoundException(
//                            Error.NOT_FOUND_USER_ONBOARDING,
//                            Error.NOT_FOUND_USER_ONBOARDING.getMessage())
//                    );
//
//            int displayCount = (int) (userOnboardingEntity.getDailyRecommendPostCount() + 5);
//
//            // 추천 풀 크기 계산 (표시 개수의 3배 또는 최소 20개)
//            int poolSize = Math.max(displayCount * 3, 20);
//
//            log.info("[추천 풀 생성] User:{} 표시개수:{} 전체풀크기:{}",
//                    loginUser.getId(), displayCount, poolSize);
//
//            List<PostEntity> allRecommendedPosts = findRecommendedPostsFromOp(poolSize, loginUser);
//
//            if (allRecommendedPosts.isEmpty()) {
//                log.warn("추천된 공고가 없습니다. User:{}", loginUser.getId());
//                return new ArrayList<>();
//            }
//
//            List<TodayApplyPostsResponseDto> allRecommendations = allRecommendedPosts.stream()
//                    .map(TodayApplyPostsResponseDto::from)
//                    .collect(Collectors.toList());
//
//            int actualDisplayCount = Math.min(displayCount, allRecommendations.size());
//
//            // 표시 리스트와 예비 풀 분리
//            List<TodayApplyPostsResponseDto> displayList = new ArrayList<>(
//                    allRecommendations.subList(0, actualDisplayCount)
//            );
//
//            List<TodayApplyPostsResponseDto> reservePool = new ArrayList<>();
//            if (allRecommendations.size() > actualDisplayCount) {
//                reservePool = new ArrayList<>(
//                        allRecommendations.subList(actualDisplayCount, allRecommendations.size())
//                );
//            }
//
//            // 로깅
//            List<Long> displayIds = displayList.stream()
//                    .map(TodayApplyPostsResponseDto::recruitmentId)
//                    .toList();
//            List<Long> reserveIds = reservePool.stream()
//                    .map(TodayApplyPostsResponseDto::recruitmentId)
//                    .toList();
//
//            log.info("[첫 요청] User:{} 표시 IDs: {}", loginUser.getId(), displayIds);
//            log.info("[첫 요청] User:{} 예비풀 IDs: {} (총 {}개)",
//                    loginUser.getId(),
//                    reserveIds.size() > 10 ? reserveIds.subList(0, 10) + "..." : reserveIds,
//                    reserveIds.size());
//
//            // 표시 리스트와 예비 풀 모두 저장
//            postRecommendationService.initializeUserRecommendations(
//                    loginUser.getId(),
//                    displayList,
//                    reservePool
//            );
//
//            return displayList;
//
//        } else {
//            // 새로고침 처리
//            List<TodayApplyPostsResponseDto> currentDisplayList =
//                    postRecommendationService.getCurrentDisplayList(loginUser.getId());
//
//            if (currentDisplayList.isEmpty()) {
//                log.warn("사용자 {}의 기존 표시 리스트가 없습니다. 첫 번째 요청으로 재처리",
//                        loginUser.getId());
//                return getTodayApplyPostList(
//                        new GetTodayApplyPostsRequest(true, loginUser.getId()),
//                        loginUser
//                );
//            }
//
//            Long targetRecruitmentId = request.requireRefreshRecruitmentId();
//
//            // 새로고침 전 로깅
//            List<Long> beforeIds = currentDisplayList.stream()
//                    .map(TodayApplyPostsResponseDto::recruitmentId)
//                    .toList();
//            log.info("[새로고침 시작] User:{} 교체대상:{} 현재IDs: {}",
//                    loginUser.getId(), targetRecruitmentId, beforeIds);
//
//            // 예비 풀에서 새 공고 가져오기
//            TodayApplyPostsResponseDto replacementPost =
//                    postRecommendationService.getAndRemoveFromReservePool(loginUser.getId());
//
//            if (replacementPost == null) {
//                log.warn("[예비풀 소진] User:{} 새로운 추천 요청 필요", loginUser.getId());
//
//                // 예비 풀이 비었으면 재충전
//                refillReservePoolForUser(loginUser, currentDisplayList);
//                replacementPost = postRecommendationService.getAndRemoveFromReservePool(loginUser.getId());
//
//                if (replacementPost == null) {
//                    log.error("[새로고침 실패] User:{} 대체 공고를 찾을 수 없음", loginUser.getId());
//                    return currentDisplayList;
//                }
//            }
//
//            // 대상 공고만 교체
//            final TodayApplyPostsResponseDto finalReplacement = replacementPost;
//            List<TodayApplyPostsResponseDto> updatedList = currentDisplayList.stream()
//                    .map(post -> post.recruitmentId().equals(targetRecruitmentId) ? finalReplacement : post)
//                    .collect(Collectors.toList());
//
//            // 새로고침 후 로깅
//            List<Long> afterIds = updatedList.stream()
//                    .map(TodayApplyPostsResponseDto::recruitmentId)
//                    .toList();
//            log.info("[새로고침 완료] User:{} 변경: {} → {} 최종IDs: {}",
//                    loginUser.getId(), targetRecruitmentId, finalReplacement.recruitmentId(), afterIds);
//
//            // 업데이트된 리스트 저장
//            postRecommendationService.updateCurrentDisplayList(loginUser.getId(), updatedList);
//
//            // 예비 풀 크기 체크 (5개 이하면 자동 재충전)
//            int remainingPoolSize = postRecommendationService.getReservePoolSize(loginUser.getId());
//            if (remainingPoolSize <= 5) {
//                log.info("[자동 재충전 트리거] User:{} 남은예비풀:{}",
//                        loginUser.getId(), remainingPoolSize);
//                refillReservePoolForUser(loginUser, updatedList);
//            }
//
//            return updatedList;
//        }
    }

    /**
     * 예비 풀 재충전 헬퍼 메서드
     */
    private void refillReservePoolForUser(UserEntity loginUser, List<TodayApplyPostsResponseDto> currentDisplayList) {
        log.info("[예비풀 재충전] User:{} 시작", loginUser.getId());

        // 현재 표시중인 ID들
        Set<Long> currentIds = currentDisplayList.stream()
                .map(TodayApplyPostsResponseDto::recruitmentId)
                .collect(Collectors.toSet());

        // 새로운 추천 요청 (현재 개수 + 추가 20개)
        int requestSize = currentIds.size() + 20;
        List<PostEntity> newRecommendations = findRecommendedPostsFromOp(requestSize, loginUser);

        // 현재 표시중이지 않은 것들만 필터링하여 예비 풀에 추가
        List<TodayApplyPostsResponseDto> newReserveItems = newRecommendations.stream()
                .filter(post -> !currentIds.contains(post.getRecruitmentId()))
                .map(TodayApplyPostsResponseDto::from)
                .collect(Collectors.toList());

        if (!newReserveItems.isEmpty()) {
            postRecommendationService.refillReservePool(loginUser.getId(), newReserveItems);
            log.info("[예비풀 재충전 완료] User:{} 추가개수:{}",
                    loginUser.getId(), newReserveItems.size());
        } else {
            log.warn("[예비풀 재충전 실패] User:{} 새로운 추천을 찾을 수 없음",
                    loginUser.getId());
        }
    }

    @Override
    public List<ApplyPostResponseDto> getAllUserApplyHistory(UserEntity loginUser) {
        List<PostApplyEntity> postApplyEntityList = postApplyEntityRepository.findAllByUserEntityWithPostFetch(loginUser);
        return postApplyEntityList.stream()
                .map(ApplyPostResponseDto::from)
                .toList();
    }

    @Override
    public void applyJobPost(PostApplyJobDto request, UserEntity loginUser) {
        PostEntity postEntity = postEntityRepository.findById(request.recruitmentId()).orElseThrow(
                () -> new NotFoundException(Error.NOT_FOUND_POST, Error.NOT_FOUND_POST.getMessage())
        );

        if (postApplyEntityRepository.existsByUserEntityAndPostEntity(loginUser, postEntity)) {
            throw new BadRequestException(Error.BAD_REQUEST_ALREADY_APPLIED, Error.BAD_REQUEST_ALREADY_APPLIED.getMessage());
        }

        postApplyEntityRepository.save(PostApplyEntity.create(loginUser, postEntity));
    }

    @Override
    public void deleteApplyPost(DeleteApplyJobDto request, UserEntity loginUser) {
        Long recruitmentId = request.recruitmentId();
        PostEntity postEntity = postEntityRepository.findById(recruitmentId).orElseThrow(
                () -> new NotFoundException(Error.NOT_FOUND_POST, Error.NOT_FOUND_POST.getMessage())
        );

        PostApplyEntity applyEntity = postApplyEntityRepository
                .findPostApplyEntityByPostEntityAndUserEntity(postEntity, loginUser)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_APPLY, Error.NOT_FOUND_APPLY.getMessage()));

        postApplyEntityRepository.delete(applyEntity);
    }

    @Override
    public void updateApplyStatus(PutPostApplyStatusDto request, UserEntity loginUser) {
        ApplyStatus newStatus = convertToApplyStatus(request.statusCode());

        Long recruitmentId = request.recruitmentId();
        PostEntity postEntity = postEntityRepository.findById(recruitmentId).orElseThrow(
                () -> new NotFoundException(Error.NOT_FOUND_POST, Error.NOT_FOUND_POST.getMessage())
        );

        PostApplyEntity applyEntity = postApplyEntityRepository
                .findPostApplyEntityByPostEntityAndUserEntity(postEntity, loginUser)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_APPLY, Error.NOT_FOUND_APPLY.getMessage()));

        applyEntity.setApplyStatus(newStatus);
    }

    @Override
    public void postUserTodayPosts(PostTodayApplyPostsRequest request, UserEntity loginUser) {
        List<Long> recruitmentIds = request.getRecruitmentIdList();

        validateInputs(recruitmentIds, loginUser);

        List<PostEntity> posts = postEntityRepository.findAllById(recruitmentIds);

        validateResults(posts, recruitmentIds);

        List<UserTodayPostEntity> entities = posts.stream()
                .map(post -> UserTodayPostEntity.create(loginUser, post))
                .collect(Collectors.toList());

        userTodayPostRepository.saveAll(entities);
    }

    private void validateInputs(List<Long> recruitmentIds, UserEntity loginUser) {
        if (recruitmentIds == null || recruitmentIds.isEmpty()) {
            throw new IllegalArgumentException("공고 ID 목록이 비어있습니다.");
        }
        if (recruitmentIds.contains(null)) {
            throw new IllegalArgumentException("null인 공고 ID가 포함되어 있습니다.");
        }
        if (loginUser == null || loginUser.getId() == null) {
            throw new IllegalArgumentException("로그인 사용자 정보가 없습니다.");
        }
    }

    private void validateResults(List<PostEntity> posts, List<Long> recruitmentIds) {
        if (posts.size() != recruitmentIds.size()) {
            Set<Long> foundIds = posts.stream()
                    .map(PostEntity::getRecruitmentId)
                    .collect(Collectors.toSet());

            List<Long> missingIds = recruitmentIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new NotFoundException(Error.NOT_FOUND_POST, Error.NOT_FOUND_POST.getMessage());
        }
    }

    private ApplyStatus convertToApplyStatus(String statusCode) {
        try {
            return ApplyStatus.fromCode(statusCode);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(Error.BAD_REQUEST_APPLY_STATUS, Error.BAD_REQUEST_APPLY_STATUS.getMessage());
        }
    }

    @Override
    public ApplyCountDto getUserApplyCount(UserEntity loginUser) {
        Long userId = loginUser.getId();
        return new ApplyCountDto(
                getTodayApplyCount(userId),
                getThisWeekApplyCount(userId),
                getTotalApplyCount(userId)
        );
    }

    private Integer getTotalApplyCount(Long userId) {
        return postApplyEntityRepository.countByUserEntityIdSafe(userId);
    }

    private Integer getTodayApplyCount(Long userId) {
        LocalDateTime[] todayRange = getTodayRange();
        return postApplyEntityRepository.getTodayApplyCount(userId, todayRange[0], todayRange[1]);
    }

    private Integer getThisWeekApplyCount(Long userId) {
        LocalDateTime[] weekRange = getThisWeekRange();
        return postApplyEntityRepository.getThisWeekApplyCount(userId, weekRange[0], weekRange[1]);
    }

    private LocalDateTime[] getTodayRange() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return new LocalDateTime[]{startOfDay, endOfDay};
    }

    private LocalDateTime[] getThisWeekRange() {
        LocalDateTime startOfWeek = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
        return new LocalDateTime[]{startOfWeek, endOfWeek};
    }

    private Page<PostEntity> findPostsByViewCount(Pageable pageable) {
        return postEntityRepository.findAll(pageable);
    }

    private List<PostEntity> findRecommendedPostsFromOp(Integer k, UserEntity loginUser) {
        List<Double> userVector = userVectorService.createUserProfileEmbedding(loginUser);

        OpenSearchDto.KnnViewResponse ViewResponse = postRecommendsFinder.knn(new OpenSearchDto.KnnReq(userVector, k));

        List<Long> recommendedIds = ViewResponse.items().stream()
                .map(OpenSearchDto.KnnView::doc_id)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .collect(Collectors.toList());

        if (recommendedIds.isEmpty()) {
            log.info("오픈서치에서 추천된 공고가 없습니다. k={}, vector size={}", k, userVector.size());
            return List.of();
        }

        log.info("오픈서치 추천 결과 - User:{} 추천ID: {}", loginUser.getId(), recommendedIds);

        return postEntityRepository.findAllById(recommendedIds);
    }


}
