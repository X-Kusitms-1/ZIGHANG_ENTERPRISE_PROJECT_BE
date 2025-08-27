package com.project.zighang.oauth2.service;

import com.project.zighang.oauth2.dto.KakaoUserInfoResponseDto;
import com.project.zighang.oauth2.dto.LoginResult;
import com.project.zighang.oauth2.dto.TokenDto;
import com.project.zighang.oauth2.dto.TokenResult;
import com.project.zighang.oauth2.repository.TokenRepository;
import com.project.zighang.user.entity.UserEntity;
import com.project.zighang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final TokenProvider tokenProvider;

    @Transactional
    public TokenResult processUserAndGetToken(KakaoUserInfoResponseDto userInfo) {
        LoginResult loginResult = loginOrSignUp(userInfo);
        UserEntity user = loginResult.user();

        Optional<TokenEntity> existingToken = tokenRepository.findByUserEntity(user);

        if (existingToken.isPresent() && tokenProvider.validateToken(existingToken.get().getAccessToken())) {
            TokenDto tokenDto = TokenDto.of(existingToken.get().getAccessToken(), existingToken.get().getRefreshToken());
            return new TokenResult(tokenDto, loginResult.isNewUser());
        }

        TokenDto newTokenDto = tokenProvider.createToken(user);
        upsertTokenEntity(newTokenDto, user);
        return new TokenResult(newTokenDto, loginResult.isNewUser());
    }

    private LoginResult loginOrSignUp(KakaoUserInfoResponseDto userInfo) {
        Long kakaoId = userInfo.getId();
        Optional<UserEntity> userOptional = userRepository.findBySocialId(kakaoId);

        if (userOptional.isEmpty()) {
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

            UserEntity newUser = userRepository.save(UserEntity.create(email, name, "kakao", kakaoId));
            return new LoginResult(newUser, true);
        } else {
            return new LoginResult(userOptional.get(), false);
        }
    }

    private void upsertTokenEntity(TokenDto tokenDto, UserEntity user) {
        Optional<TokenEntity> existing = tokenRepository.findByUserEntity(user);
        if (existing.isPresent()) {
            existing.get().updateTokens(tokenDto.accessToken(), tokenDto.refreshToken());
        } else {
            tokenRepository.save(TokenEntity.create(tokenDto.accessToken(), tokenDto.refreshToken(), user));
        }
    }
}