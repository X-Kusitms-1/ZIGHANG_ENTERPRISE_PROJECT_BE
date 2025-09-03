package com.project.zighang.post.service;

import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.post.repository.PostEntityRepository;
import com.project.zighang.post.dto.PostResponseDto;
import com.project.zighang.post.entity.PostEntity;
import com.project.zighang.user.entity.UserEntity;
import com.project.zighang.user.entity.UserOnboardingEntity;
import com.project.zighang.user.repository.UserOnboardingRepository;
import com.project.zighang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserOnboardingRepository userOnboardingRepository;
    private final UserRepository userRepository;

    private static final int MAX_SIZE = 50;

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPostList(int page, int size) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_SIZE);
        int normalizedPage = Math.max(page, 0);
        return getAllPostListByRepository(normalizedPage, normalizedSize)
                .map(PostResponseDto::from);
    }

    private Page<PostEntity> getAllPostListByRepository(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("viewCount").descending());
        return postEntityRepository.findAll(pageable);
    }

    @Override
    public List<PostResponseDto> getTodayApplyPostList(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(
                        com.project.zighang.global.exception.Error.NOT_FOUND_USER, com.project.zighang.global.exception.Error.NOT_FOUND_USER.getMessage())
        );
        UserOnboardingEntity userOnboardingEntity = userOnboardingRepository.findByUserEntity(userEntity).orElseThrow(
                () -> new NotFoundException(
                        com.project.zighang.global.exception.Error.NOT_FOUND_USER_ONBOARDING, Error.NOT_FOUND_USER_ONBOARDING.getMessage())
        );

        Long applyPostCount = userOnboardingEntity.getDailyRecommendPostCount();
        if (applyPostCount != null && applyPostCount >= 0) {
            return getAllPostListByRepository(Math.toIntExact(applyPostCount))
                    .stream()
                    .map(PostResponseDto::from)
                    .toList();
        }

        return List.of();
    }

    private List<PostEntity> getAllPostListByRepository(int applyPostCount) {
        Pageable pageable = PageRequest.of(0, applyPostCount, Sort.by("viewCount").descending());
        return postEntityRepository.findAll(pageable).getContent();
    }
}
