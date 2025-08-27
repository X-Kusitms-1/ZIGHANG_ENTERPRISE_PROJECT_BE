package com.project.zighang.oauth2.service;

import com.project.zighang.oauth2.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final WebClient.Builder webClientBuilder;
    private final UserAuthService userAuthService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    @Transactional
    public TokenResult login(String code) {
        KakaoUserInfoResponseDto userInfo = getUserInfoByCode(code);
        return userAuthService.processUserAndGetToken(userInfo);
    }

    private KakaoUserInfoResponseDto getUserInfoByCode(String code) {
        KakaoTokenResponseDto tokenResponse = getToken(code);
        return getUserInfo(tokenResponse.getAccess_token());
    }

    private KakaoTokenResponseDto getToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        WebClient webClient = webClientBuilder.baseUrl(tokenUri).build();

        return webClient.post()
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();
    }

    private KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        WebClient webClient = webClientBuilder.baseUrl(userInfoUri)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();

        return webClient.get()
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }
}
