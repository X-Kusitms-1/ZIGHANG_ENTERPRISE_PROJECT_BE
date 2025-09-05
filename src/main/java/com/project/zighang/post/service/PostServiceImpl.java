package com.project.zighang.post.service;

import com.project.zighang.post.repository.PostEntityRepository;
import com.project.zighang.post.dto.PostResponseDto;
import com.project.zighang.post.entity.PostEntity;
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
}
