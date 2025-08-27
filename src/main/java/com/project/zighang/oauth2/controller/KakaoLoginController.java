package com.project.zighang.oauth2.controller;

import com.project.zighang.oauth2.dto.TokenResult;
import com.project.zighang.oauth2.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth/kakao")
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @GetMapping
    @Operation(
            summary = "카카오 소셜 로그인",
            description = """
    카카오 인가 코드로 소셜 로그인을 진행하며, 신규 유저일 경우 자동 가입됩니다. 성공 시 서비스 자체 JWT(Access/Refresh Token)와 신규 가입 여부를 반환합니다.
    """
    )
    public ResponseEntity<TokenResult> kakaoLogin(
            @NotBlank @RequestParam("code") String code) {
        TokenResult result = kakaoLoginService.login(code);
        return ResponseEntity.ok(result);
    }
}