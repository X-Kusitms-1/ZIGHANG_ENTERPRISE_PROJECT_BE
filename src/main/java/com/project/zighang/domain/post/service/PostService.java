package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.dto.*;
import com.project.zighang.domain.user.entity.UserEntity;
import com.project.zighang.global.client.objectStorage.dto.PreSignedUrlResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    Page<PostResponseDto> getAllPostList(int page, int size);

    List<TodayApplyPostsResponseDto> getTodayApplyPostList(UserEntity loginUser);

    List<ApplyPostResponseDto> getAllUserApplyHistory(UserEntity loginUser);

    void applyJobPost(PostApplyJobDto request, UserEntity loginUser);

    ApplyCountDto getUserApplyCount(UserEntity loginUser);

    void deleteApplyPost(DeleteApplyJobDto request, UserEntity loginUser);

    void updateApplyStatus(PutPostApplyStatusDto request, UserEntity loginUser);

    void postUserTodayPosts(PostTodayApplyPostsRequest request, UserEntity loginUser);
}
