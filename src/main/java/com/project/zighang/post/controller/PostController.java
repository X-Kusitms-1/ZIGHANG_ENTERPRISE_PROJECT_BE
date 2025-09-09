package com.project.zighang.post.controller;

import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.Success;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.global.exception.template.RspTemplate;
import com.project.zighang.post.dto.PageDto;
import com.project.zighang.post.dto.PostApplyJobDto;
import com.project.zighang.post.dto.PostResponseDto;
import com.project.zighang.post.service.PostService;
import com.project.zighang.user.entity.UserEntity;
import com.project.zighang.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

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
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        List<PostResponseDto> todayApplyPosts = postService.getTodayApplyPostList(loginUser);
        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, todayApplyPosts);
    }

    @PostMapping("/apply")
    @Operation(summary = "공고 지원", description = "이미 지원한 공고는 다시 지원할 수 없습니다.")
    public RspTemplate<?> applyInJobPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PostApplyJobDto request
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        postService.applyJobPost(request, loginUser);
        return RspTemplate.success(Success.POST_JOB_APPLY);
    }

    @GetMapping("/apply-history")
    @Operation(summary = "지난 지원 목록 조회")
    public RspTemplate<?> getUserApplyHistory(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        List<PostResponseDto> userApplyHistory = postService.getAllUserApplyHistory(loginUser);
        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, userApplyHistory);
    }

    private UserEntity getUserEntityFromUserDetailsImpl(UserDetailsImpl userDetails) throws RuntimeException {
        if (userDetails == null) {
            throw new NotFoundException(com.project.zighang.global.exception.Error.NOT_FOUND_USER, Error.NOT_FOUND_USER.getMessage());
        }
        return userDetails.getUserEntity();
    }
}
