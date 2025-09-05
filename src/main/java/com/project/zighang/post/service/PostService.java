package com.project.zighang.post.service;

import com.project.zighang.post.dto.PostResponseDto;
import org.springframework.data.domain.Page;

public interface PostService {
    Page<PostResponseDto> getAllPostList(int page, int size);
}
