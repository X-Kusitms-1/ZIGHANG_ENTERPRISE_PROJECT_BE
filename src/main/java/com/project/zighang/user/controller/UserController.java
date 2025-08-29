package com.project.zighang.user.controller;

import com.project.zighang.global.exception.Success;
import com.project.zighang.global.template.RspTemplate;
import com.project.zighang.user.dto.PostUserOnboardingDto;
import com.project.zighang.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "사용자 온보딩 정보")
    @PostMapping("/onboarding")
    public RspTemplate<Void> postUserOnboardingInfo(
            @RequestBody PostUserOnboardingDto postUserOnboardingDto
    ) {
        userService.addUserOnboardingInfo(postUserOnboardingDto);
        return RspTemplate.success(Success.POST_USER_Onboarding_API_REQUEST_SUCCESS);
    }
}
