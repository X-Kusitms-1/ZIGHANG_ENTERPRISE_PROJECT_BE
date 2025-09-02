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

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostEntityRepository postEntityRepository;

    private static final int MAX_SIZE = 50;

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPostList(int page, int size) {
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }
        return getAllPostListByRepository(page, size).map(PostResponseDto::from);
    }

    private Page<PostEntity> getAllPostListByRepository(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("score").descending());
        return postEntityRepository.findAll(pageable);
    }
}
