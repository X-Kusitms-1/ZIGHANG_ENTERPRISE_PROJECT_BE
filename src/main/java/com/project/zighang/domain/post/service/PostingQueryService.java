package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.entity.PostApplyEntity;
import com.project.zighang.domain.post.entity.PostEntity;
import com.project.zighang.domain.post.enumerate.ApplyStatus;
import com.project.zighang.domain.post.repository.PostApplyEntityRepository;
import com.project.zighang.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostingQueryService implements PostingFinder {

    private final PostApplyEntityRepository postApplyEntityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PostEntity> findPostingsByStatus(UserEntity user, ApplyStatus status) {
        List<PostApplyEntity> applies = postApplyEntityRepository
                .findAllByUserEntityAndApplyStatusWithPostFetch(user, status);

        return applies.stream()
                .map(PostApplyEntity::getPostEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostEntity> findPostingsByStatusAndDateRange(UserEntity user, ApplyStatus status, LocalDate startDate, LocalDate endDate) {
        // LocalDate를 LocalDateTime으로 변환 (시작일은 00:00:00, 종료일은 23:59:59)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<PostApplyEntity> postApplyEntities =
                postApplyEntityRepository.findAllByUserEntityAndApplyStatusAndCreatedAtBetweenWithPostFetch(
                        user, status, startDateTime, endDateTime);

        return postApplyEntities.stream()
                .map(PostApplyEntity::getPostEntity)
                .collect(Collectors.toList());
    }
}