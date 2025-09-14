package com.project.zighang.domain.post.controller;

import com.project.zighang.domain.post.dto.*;
import com.project.zighang.domain.post.service.ResumeFileService;
import com.project.zighang.global.client.objectStorage.service.NcpPresignedUrlReader;
import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.Success;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.global.exception.template.RspTemplate;
import com.project.zighang.domain.post.service.PostService;
import com.project.zighang.domain.user.entity.UserEntity;
import com.project.zighang.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ResumeFileService resumeFileService;

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

    @PostMapping("/today-apply/recommend")
    @Operation(summary = "오늘의 추천 공고 목록 조회",
            description = "선택하기 전에 추천하는 리스트입니다. 지원할 공고 개수보다 5개 더 보입니다. " +
                    "isFirstApiCall = true의 경우 모든 응답이 새로운 데이터로 내려갑니다. isFirstApiCall = false의 경우 requireRefreshRecruitmentId로 받은 공고 데이터만 새로운 데이터로 대체되어 내려갑니다."
    )
    public RspTemplate<?> getTodayApplyPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody GetTodayApplyPostsRequest request
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        List<TodayApplyPostsResponseDto> todayApplyPosts = postService.getTodayApplyPostList(request, loginUser);
        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, todayApplyPosts);
    }

    @PostMapping("/today-apply")
    @Operation(summary = "오늘의 추천 공고 목록 추가", description = "추천 받은 공고들중 오늘의 추천 리스트에 넣을 공고를 입력합니다.")
    public RspTemplate<?> postTodayApplyPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PostTodayApplyPostsRequest request
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        postService.postUserTodayPosts(request, loginUser);
        return RspTemplate.success(Success.POST_TODAY_APPLY);
    }

    @PostMapping("/apply")
    @Operation(summary = "공고 지원 추가", description = "이미 지원한 공고는 다시 지원할 수 없습니다.")
    public RspTemplate<?> applyInJobPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PostApplyJobDto request
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        postService.applyJobPost(request, loginUser);
        return RspTemplate.success(Success.POST_JOB_APPLY);
    }

    @DeleteMapping("/apply")
    @Operation(summary = "공고 지원 삭제", description = "지원한 공고를 취소합니다.")
    public RspTemplate<?> deleteJobApply(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody DeleteApplyJobDto request
            ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        postService.deleteApplyPost(request, loginUser);
        return RspTemplate.success(Success.DELETE_JOB_APPLY);
    }

    @GetMapping("/apply-history")
    @Operation(summary = "지난 지원 목록 조회")
    public RspTemplate<?> getUserApplyHistory(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        List<ApplyPostResponseDto> userApplyHistory = postService.getAllUserApplyHistory(loginUser);
        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, userApplyHistory);
    }

    @Operation(summary = "지원 개수 현황 확인" , description = "오늘 지원한 곳 개수, 이번 주 지원 개수, 누적 지원 개수를 반환합니다.")
    @GetMapping(value = "/apply")
    public RspTemplate<?> getApplyCount(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        ApplyCountDto applyCountDto = postService.getUserApplyCount(loginUser);
        return RspTemplate.success(Success.GET_API_REQUEST_SUCCESS, applyCountDto);
    }

    @PutMapping("/apply-status")
    @Operation(
            summary = "공고 합격 상태 변경",
            description = """
        지원한 공고의 합격 여부를 변경합니다.
        
        **recruitmentId**
        - 해당 공고의 recruitmentId
        
        **statusCode:**
        - pending: 대기중 (심사 중)
        - passed: 합격
        - rejected: 불합격 (탈락)
        """
    )
    public RspTemplate<?> updateApplyStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PutPostApplyStatusDto request
    ) {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        postService.updateApplyStatus(request, loginUser);
        return RspTemplate.success(Success.PUT_APPLY_STATUS);
    }

    @PostMapping("/resume/upload-complete")
    public RspTemplate<?> completeUpload(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UploadCompleteRequestDto request
    ) throws FileUploadException {
        UserEntity loginUser = getUserEntityFromUserDetailsImpl(userDetails);
        ResumeFileResponseDto response = resumeFileService.completeUpload(request, loginUser);
        return RspTemplate.success(Success.POST_RESUME, response);
    }

    private UserEntity getUserEntityFromUserDetailsImpl(UserDetailsImpl userDetails) throws RuntimeException {
        if (userDetails == null) {
            throw new NotFoundException(com.project.zighang.global.exception.Error.NOT_FOUND_USER, Error.NOT_FOUND_USER.getMessage());
        }
        return userDetails.getUserEntity();
    }
}
