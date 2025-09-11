package com.project.zighang.domain.post.service;

import com.project.zighang.domain.post.dto.PostApplyJobDto;
import com.project.zighang.domain.post.dto.PostResponseDto;
import com.project.zighang.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    Page<PostResponseDto> getAllPostList(int page, int size);

    List<PostResponseDto> getTodayApplyPostList(UserEntity loginUser);

    List<PostResponseDto> getAllUserApplyHistory(UserEntity loginUser);

    void applyJobPost(PostApplyJobDto request, UserEntity loginUser);
}
