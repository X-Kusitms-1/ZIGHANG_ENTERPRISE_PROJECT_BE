package com.project.zighang.oauth2.service;

import com.project.zighang.oauth2.dto.*;
import com.project.zighang.oauth2.repository.TokenRepository;
import com.project.zighang.user.entity.UserEntity;
import com.project.zighang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final WebClient.Builder webClientBuilder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;

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
        LoginResult result = loginOrSignUp(userInfo);
        TokenDto tokenDto = tokenProvider.createToken(result.user());
        saveTokenEntity(tokenDto, result.user());
        return TokenResult.from(tokenDto, result.isNewUser());
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

    @Transactional
    protected LoginResult loginOrSignUp(KakaoUserInfoResponseDto userInfo) {
        Long kakaoId = userInfo.getId();
        String email = null;
        String name = null;

        if (userInfo.getKakaoAccount() != null) {
            email = userInfo.getKakaoAccount().getEmail();
            if (userInfo.getKakaoAccount().getProfile() != null) {
                name = userInfo.getKakaoAccount().getProfile().getNickname();
            }
        }

        if (name == null || name.isEmpty()) {
            name = "카카오 사용자";
        }

        Optional<UserEntity> userOptional = userRepository.findBySocialId(kakaoId);
        if (userOptional.isEmpty()) {
            UserEntity newUser = userRepository.save(
                    UserEntity.create(email, name, "kakao", kakaoId)
            );
            return new LoginResult(newUser, true);
        } else {
            return new LoginResult(userOptional.get(), false);
        }
    }

    @Transactional(readOnly = true)
    protected Optional<UserEntity> getUserByKaKaoId(Long kakaoId) {
        return userRepository.findBySocialId(kakaoId);
    }

    @Transactional
    protected void saveTokenEntity(TokenDto tokenDto, UserEntity userEntity) {
        TokenEntity tokenEntity = TokenEntity.create(tokenDto.accessToken(), tokenDto.refreshToken(), userEntity);
        tokenRepository.save(tokenEntity);
    }
}
