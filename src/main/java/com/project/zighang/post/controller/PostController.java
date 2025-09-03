package com.project.zighang.post.controller;

import com.project.zighang.global.exception.Success;
import com.project.zighang.global.template.RspTemplate;
import com.project.zighang.post.dto.GetTodayApplyPostDto;
import com.project.zighang.post.dto.PageDto;
import com.project.zighang.post.dto.PostResponseDto;
import com.project.zighang.post.service.PostServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-post")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl postService;

    @GetMapping
    @Operation(summary = "공고 전체 목록 조회", description = "채용공고 목록을 페이지네이션하여 조회합니다. score 기반 내림차순 정렬입니다.")
    @Parameters({
            @Parameter(name = "page", description = "요청할 페이지 번호 (1부터 시작)"),
            @Parameter(name = "size", description = "한 페이지에 보여줄 공고 수, 최대 50개이며 초과시 50개로 조회됨")
    })
    public RspTemplate<PageDto<?>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        int pageInIndex = Math.max(page, 1) - 1;
        int safeSize = Math.max(size, 1);
        Page<PostResponseDto> postPage = postService.getAllPostList(pageInIndex, safeSize);
        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, PageDto.from(postPage));
    }

    @GetMapping("/today-apply")
    @Operation(summary = "오늘의 추천 공고 목록 조회")
    public RspTemplate<?> getTodayApplyPosts(
            @RequestParam Long userId
    ) {
        List<PostResponseDto> todayApplyPosts = postService.getTodayApplyPostList(userId);
        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, todayApplyPosts);
    }
}
