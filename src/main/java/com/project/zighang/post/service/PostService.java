package com.project.zighang.post.service;

import com.project.zighang.post.dto.GetTodayApplyPostDto;
import com.project.zighang.post.dto.PostResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    Page<PostResponseDto> getAllPostList(int page, int size);
    List<PostResponseDto> getTodayApplyPostList(Long userId);
}
