package com.project.zighang.user.controller;

import com.project.zighang.global.exception.Success;
import com.project.zighang.global.template.RspTemplate;
import com.project.zighang.user.dto.PostUserOnboardingDto;
import com.project.zighang.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/onboarding")
    @Operation(
            summary = "사용자 온보딩 정보 저장",
            description = "사용자의 온보딩 정보(경력, 관심지역 목록, 관심산업 목록)를 저장합니다. "
+ "기존 저장 데이터가 있으면 모두 삭제 후 새로 받은 정보로 대체합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 온보딩 POST API 호출에 성공했습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 정보입니다.")
    })
    public RspTemplate<Void> postUserOnboardingInfo(
            @RequestBody PostUserOnboardingDto postUserOnboardingDto
    ) {
        userService.addUserOnboardingInfo(postUserOnboardingDto);
        return RspTemplate.success(Success.POST_USER_Onboarding_API_REQUEST_SUCCESS);
    }
}
