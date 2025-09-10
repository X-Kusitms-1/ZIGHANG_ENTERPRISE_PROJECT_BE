package com.project.zighang.post.service;

import com.project.zighang.post.entity.PostApplyEntity;
import com.project.zighang.post.enumerate.ApplyStatus;
import com.project.zighang.post.repository.PostApplyEntityRepository;
import com.project.zighang.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostingQueryService implements PostingFinder {

    private final PostApplyEntityRepository postApplyEntityRepository;

    @Transactional(readOnly = true)
    public List<PostApplyEntity> findPostingsByStatus(UserEntity user, ApplyStatus status) {
        return postApplyEntityRepository
                .findAllByUserEntityAndApplyStatusWithPostFetch(user, status);
    }
}