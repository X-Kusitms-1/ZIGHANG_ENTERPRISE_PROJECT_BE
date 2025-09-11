package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.dto.PostResumeRequestDto;
import com.project.zighang.domain.post.dto.ResumeResponse;
import com.project.zighang.global.client.objectStorage.dto.PreSignedUrlResponse;
import com.project.zighang.global.client.objectStorage.service.NcpPresignedUrlReader;
import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.model.BadRequestException;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.domain.post.dto.PostApplyJobDto;
import com.project.zighang.domain.post.entity.PostApplyEntity;
import com.project.zighang.domain.post.repository.PostApplyEntityRepository;
import com.project.zighang.domain.post.repository.PostEntityRepository;
import com.project.zighang.domain.post.dto.PostResponseDto;
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
    public List<PostResponseDto> getAllUserApplyHistory(UserEntity loginUser) {
        List<PostApplyEntity> postApplyEntityList = postApplyEntityRepository.findAllByUserEntityWithPostFetch(loginUser);
        return postApplyEntityList.stream()
                .map(PostApplyEntity::getPostEntity)
                .map(PostResponseDto::from)
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

        return null;
    }

    private Page<PostEntity> findPostsByViewCount(Pageable pageable) {
        return postEntityRepository.findAll(pageable);
    }

    private List<PostEntity> findTopNPostsByViewCount(int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("viewCount").descending());
        return postEntityRepository.findAll(pageable).getContent();
    }
}
