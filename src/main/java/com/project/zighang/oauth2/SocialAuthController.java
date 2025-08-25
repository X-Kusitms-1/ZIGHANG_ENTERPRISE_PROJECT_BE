package com.project.zighang.oauth2;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth2")
@Tag(name = "회원가입/로그인 API", description = "회원가입 로그인과 관련된 API들입니다. access-refresh 토큰 형태로 진행됩니다.")
public class SocialAuthController {

    private final AuthService authService;
}
