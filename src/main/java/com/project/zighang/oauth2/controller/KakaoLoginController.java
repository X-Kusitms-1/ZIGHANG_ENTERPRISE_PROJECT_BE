package com.project.zighang.oauth2.controller;

import com.project.zighang.oauth2.dto.TokenResult;
import com.project.zighang.oauth2.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1/auth/kakao")
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @Value("${login.redirect-url.frontend}")
    private String frontendRedirectUrl;

    @GetMapping
    @Operation(
            summary = "카카오 소셜 로그인",
            description = """
    카카오 인가 코드로 소셜 로그인을 진행합니다.
    성공 시, 서비스 자체 JWT(Access/Refresh Token)는 응답의 HttpOnly 쿠키에 설정됩니다.
    이후, isNewUser 값을 쿼리 파라미터로 포함하여 프론트엔드의 콜백 URL로 리다이렉트합니다.
    """
    )
    public void kakaoLoginAndRedirect(
            @NotBlank @RequestParam("code") String code,
            HttpServletResponse response
    ) throws IOException {
        TokenResult result = kakaoLoginService.login(code);
        Cookie accessTokenCookie = new Cookie("accessToken", result.tokenDto().accessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge(60 * 60 * 24);

        Cookie refreshTokenCookie = new Cookie("refreshToken", result.tokenDto().refreshToken());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        String redirectUrl = String.format(
                "%s?isNewUser=%b",
                frontendRedirectUrl,
                result.isNewUser()
        );
        response.sendRedirect(redirectUrl);
    }
}