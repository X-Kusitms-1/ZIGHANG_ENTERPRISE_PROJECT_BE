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

        // 1. 사용자 온보딩 정보에서 하루에 보여줄 공고 개수를 가져옵니다.
        UserOnboardingEntity userOnboardingEntity = userOnboardingRepository.findByUserEntity(loginUser)
                .orElseThrow(() -> new NotFoundException(
                        Error.NOT_FOUND_USER_ONBOARDING,
                        Error.NOT_FOUND_USER_ONBOARDING.getMessage())
                );

        // 2. 표시할 공고 개수를 설정합니다. (기존 로직: 추천 개수 + 5)
        int displayCount = (int) (userOnboardingEntity.getDailyRecommendPostCount() + 5);

        log.info("[조회수 순 조회] User:{} 요청개수:{}", loginUser.getId(), displayCount);

        // 3. PostRepository를 사용하여 조회수(viewCount)가 높은 순으로 공고를 조회합니다.
        List<PostEntity> topPosts = postEntityRepository.findAllByOrderByViewCountDesc(
                PageRequest.of(0, displayCount)
        );

        if (topPosts.isEmpty()) {
            log.warn("조회할 공고가 없습니다. User:{}", loginUser.getId());
            return new ArrayList<>();
        }

        // 4. 조회된 PostEntity 리스트를 DTO 리스트로 변환하여 반환합니다.
        return topPosts.stream()
                .map(TodayApplyPostsResponseDto::from)
                .collect(Collectors.toList());

//        long applyPostCount = 1;
//        if (request.isFirstApiCall()) {
//            // first time
//            UserOnboardingEntity userOnboardingEntity = userOnboardingRepository.findByUserEntity(loginUser).orElseThrow(
//                    () -> new NotFoundException(
//                            Error.NOT_FOUND_USER_ONBOARDING, Error.NOT_FOUND_USER_ONBOARDING.getMessage())
//            );
//
//            applyPostCount = userOnboardingEntity.getDailyRecommendPostCount() + 5;
//
//            // TASK - must switch findTopNPostsByViewCount to recommend module
//            List<TodayApplyPostsResponseDto> todayApplyPostsResponseDtoList = findRecommendedPostsFromOp((int)applyPostCount, loginUser)
//                    .stream()
//                    .map(TodayApplyPostsResponseDto::from)
//                    .toList();
//
//            log.info("추천 결과: {}", todayApplyPostsResponseDtoList);
//
//            // 초기 ID 리스트 로그
//            List<Long> idList = todayApplyPostsResponseDtoList.stream()
//                    .map(TodayApplyPostsResponseDto::recruitmentId)
//                    .toList();
//            log.info("[첫 요청] User:{} IDs: {}", loginUser.getId(), idList);
//
//            postRecommendationService.deleteAndCreateUserRecommendations(loginUser.getId(), todayApplyPostsResponseDtoList);
//
//            return todayApplyPostsResponseDtoList;
//
//        } else {
//            List<TodayApplyPostsResponseDto> existingPosts = postRecommendationService.getRecommendations(loginUser.getId());
//
//            if (existingPosts.isEmpty()) {
//                log.warn("사용자 {}의 기존 메모리 데이터가 없습니다. 첫 번째 요청으로 처리", loginUser.getId());
//                return getTodayApplyPostList(new GetTodayApplyPostsRequest(true, loginUser.getId()), loginUser);
//            }
//
//            Long requiredRefreshRecruitmentId = request.requireRefreshRecruitmentId();
//
//            // 새로고침 전 ID 리스트
//            List<Long> beforeIds = existingPosts.stream()
//                    .map(TodayApplyPostsResponseDto::recruitmentId)
//                    .toList();
//            log.info("[새로고침 전] User:{} 교체대상:{} IDs: {}",
//                    loginUser.getId(), requiredRefreshRecruitmentId, beforeIds);
//
//            Set<Long> currentListIds = existingPosts.stream()
//                    .map(TodayApplyPostsResponseDto::recruitmentId)
//                    .collect(Collectors.toSet());
//
//            List<TodayApplyPostsResponseDto> updatedPosts = existingPosts.stream()
//                    .map(post -> {
//                        if (post.recruitmentId().equals(requiredRefreshRecruitmentId)) {
//                            TodayApplyPostsResponseDto newPost = getReplacementPost(
//                                    loginUser.getId(),
//                                    requiredRefreshRecruitmentId,
//                                    currentListIds
//                            );
//                            return newPost != null ? newPost : post;
//                        }
//                        return post;
//                    })
//                    .toList();
//
//            // 새로고침 후 ID 리스트
//            List<Long> afterIds = updatedPosts.stream()
//                    .map(TodayApplyPostsResponseDto::recruitmentId)
//                    .toList();
//            log.info("[새로고침 후] User:{} IDs: {}", loginUser.getId(), afterIds);
//
//            // 변경 체크
//            for (int i = 0; i < beforeIds.size(); i++) {
//                if (!beforeIds.get(i).equals(afterIds.get(i))) {
//                    if (beforeIds.get(i).equals(requiredRefreshRecruitmentId)) {
//                        log.info("   ↳ [{}]번 인덱스 정상 교체: {} → {}",
//                                i, beforeIds.get(i), afterIds.get(i));
//                    } else {
//                        log.error("   ↳ [{}]번 인덱스 비정상 변경: {} → {}",
//                                i, beforeIds.get(i), afterIds.get(i));
//                    }
//                }
//            }
//
//            postRecommendationService.deleteAndCreateUserRecommendations(
//                    loginUser.getId(),
//                    updatedPosts
//            );
//
//            return updatedPosts;
//        }
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

    private List<PostEntity> findTopNPostsByViewCount(int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("viewCount").descending());
        return postEntityRepository.findAll(pageable).getContent();
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
