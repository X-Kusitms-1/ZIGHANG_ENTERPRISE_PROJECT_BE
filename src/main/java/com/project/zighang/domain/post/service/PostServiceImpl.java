package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.dto.*;
import com.project.zighang.domain.post.enumerate.ApplyStatus;
import com.project.zighang.global.client.objectStorage.dto.PreSignedUrlResponse;
import com.project.zighang.global.client.objectStorage.service.NcpPresignedUrlReader;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserOnboardingRepository userOnboardingRepository;
    private final PostApplyEntityRepository postApplyEntityRepository;

    private final NcpPresignedUrlReader presignedUrlReader;

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
    public List<PostResponseDto> getTodayApplyPostList(UserEntity loginUser) {
        UserOnboardingEntity userOnboardingEntity = userOnboardingRepository.findByUserEntity(loginUser).orElseThrow(
                () -> new NotFoundException(
                        Error.NOT_FOUND_USER_ONBOARDING, Error.NOT_FOUND_USER_ONBOARDING.getMessage())
        );

        Long applyPostCount = userOnboardingEntity.getDailyRecommendPostCount();
        if (applyPostCount != null && applyPostCount >= 0) {
            return findTopNPostsByViewCount(Math.toIntExact(applyPostCount))
                    .stream()
                    .map(PostResponseDto::from)
                    .toList();
        }

        return List.of();
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
    public ResumeResponse postResumeFile(PostResumeRequestDto request, UserEntity loginUser) {
        String prefix = request.prefix();
        String fileName = request.fileName();

        PreSignedUrlResponse preSignedUrlResponse = presignedUrlReader.getPreSignedUrl(prefix, fileName);

        // need to work

        return null;
    }

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
        ApplyStatus newStatus;
        try {
            newStatus = request.getApplyStatus();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(Error.BAD_REQUEST_APPLY_STATUS, Error.BAD_REQUEST_APPLY_STATUS.getMessage());
        }

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
    public ApplyCountDto getUserApplyCount(UserEntity loginUser) {
        Long userId = loginUser.getId();
        return new ApplyCountDto(
                getTodayApplyCount(userId),
                getThisWeekApplyCount(userId),
                getTotalApplyCount(userId)
        );
    }

    private Integer getTotalApplyCount(Long userId) {
        return postApplyEntityRepository.countByUserEntityId(userId);
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
}
