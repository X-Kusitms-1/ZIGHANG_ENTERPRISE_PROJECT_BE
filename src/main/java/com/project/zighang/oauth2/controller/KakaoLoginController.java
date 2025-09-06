package com.project.zighang.oauth2.controller;

import com.project.zighang.oauth2.dto.TokenResult;
import com.project.zighang.oauth2.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseCookie;

import java.io.IOException;

@Slf4j
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
        log.info("Kakao login callback received. Authorization code processed.");
        TokenResult result = kakaoLoginService.login(code);
        log.info("Kakao login successful. New user status: {}", result.isNewUser());

        ResponseCookie accessTokenCookie = createCookie("accessToken", result.tokenDto().accessToken(), 60 * 60 * 24);
        ResponseCookie refreshTokenCookie = createCookie("refreshToken", result.tokenDto().refreshToken(), 60 * 60 * 24 * 7);

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        String redirectUrl = String.format(
                "%s",
                frontendRedirectUrl
        );

        log.info("Redirecting user to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
    
    private ResponseCookie createCookie(String key, String value, int maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(key, value)
                .path("/")
                .maxAge(maxAge);
//                .httpOnly(true);

        builder.secure(true).sameSite("None");
        return builder.build();
    }
}