package com.project.zighang.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final UserEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 역할(Role) 기반 권한이 필요하다면 여기에 로직을 추가합니다.
        return Collections.emptyList();
    }

    public Long getId() {
        return userEntity.getId();
    }

    @Override
    public String getPassword() {
        return null; // JWT 방식에서는 사용하지 않습니다.
    }

    @Override
    public String getUsername() {
        return String.valueOf(userEntity.getId());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
