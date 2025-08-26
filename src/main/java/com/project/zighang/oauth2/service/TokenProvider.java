package com.project.zighang.oauth2.service;

import com.project.zighang.oauth2.dto.TokenDto;
import com.project.zighang.user.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TokenProvider {

    private final Key key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValidityTime,
                         @Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenValidityTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityTime = accessTokenValidityTime;
        this.refreshTokenValidityTime = refreshTokenValidityTime;
    }

    public TokenDto createToken(UserEntity user) {
        long nowTime = (new Date()).getTime();

        Date tokenExpiredTime = new Date(nowTime + accessTokenValidityTime);
        Date refreshExpiredTime = new Date(nowTime + refreshTokenValidityTime);

        String accessToken = Jwts.builder()
                .setSubject(user.getId().toString())
                .setExpiration(tokenExpiredTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(refreshExpiredTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.of(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        // 권한이 없으므로 빈 리스트를 전달
        List<GrantedAuthority> authorities = Collections.emptyList();

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}