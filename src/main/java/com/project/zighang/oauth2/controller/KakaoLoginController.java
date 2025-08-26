package com.project.zighang.oauth2.controller;

import com.project.zighang.oauth2.dto.TokenResult;
import com.project.zighang.oauth2.service.KakaoLoginService;
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
    public ResponseEntity<TokenResult> kakaoLogin(
            @RequestParam("code") String code) {
        TokenResult result = kakaoLoginService.login(code);
        return ResponseEntity.ok(result);
    }
}