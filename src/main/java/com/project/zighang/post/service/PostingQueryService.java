package com.project.zighang.post.service;

import com.project.zighang.post.entity.PostApplyEntity;
import com.project.zighang.post.entity.PostEntity;
import com.project.zighang.post.enumerate.ApplyStatus;
import com.project.zighang.post.repository.PostApplyEntityRepository;
import com.project.zighang.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
}