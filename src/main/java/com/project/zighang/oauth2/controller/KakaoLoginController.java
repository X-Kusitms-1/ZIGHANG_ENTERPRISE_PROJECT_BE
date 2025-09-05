package com.project.zighang.oauth2.controller;

import com.project.zighang.oauth2.dto.TokenResult;
import com.project.zighang.oauth2.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    @Operation(
            summary = "카카오 소셜 로그인",
            description = """
    카카오 인가 코드로 소셜 로그인을 진행하며, 성공 시 서비스 자체 JWT(Access/Refresh Token)와 신규 가입 여부를 반환합니다.
    처음 로그인한 유저의 경우 isNewUser가 참으로 내려갑니다.
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
                "http://localhost:3000/auth/kakao/callback?isNewUser=%b",
                result.isNewUser()
        );
        response.sendRedirect(redirectUrl);
    }
}