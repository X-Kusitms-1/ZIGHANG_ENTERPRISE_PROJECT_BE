package com.project.zighang.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.zighang.oauth2.dto.KakaoTokenResponseDto;
import com.project.zighang.oauth2.dto.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    public String login(String code) {
        KakaoTokenResponseDto tokenResponse = getToken(code);
        System.out.println("--- Kakao User Token ---");
        System.out.println(tokenResponse.getAccess_token());

        KakaoUserInfoResponseDto userInfo = getUserInfo(tokenResponse.getAccess_token());

        try {
            String userInfoJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userInfo);
            System.out.println("--- Kakao User Info ---");
            System.out.println(userInfoJson);
            System.out.println("-----------------------");
        } catch (JsonProcessingException e) {
            System.err.println("JSON 파싱 에러: " + e.getMessage());
        }

        // 카카오 사용자 정보를 기반으로 우리 서비스의 회원인지 확인
        Long kakaoId = userInfo.getId();
        String nickname = "default_nickname";

        if (userInfo.getKakaoAccount() != null && userInfo.getKakaoAccount().getProfile() != null) {
            nickname = userInfo.getKakaoAccount().getProfile().getNickname();
        }

        System.out.println("카카오 아이디: " + kakaoId);
        System.out.println("카카오 닉네임: " + nickname);

        return "로그인 성공! Kakao ID: " + kakaoId;
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
