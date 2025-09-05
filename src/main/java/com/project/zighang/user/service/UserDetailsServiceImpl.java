package com.project.zighang.user.service;

import com.project.zighang.global.exception.Error;
import com.project.zighang.global.exception.model.NotFoundException;
import com.project.zighang.user.entity.UserEntity;
import com.project.zighang.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws NotFoundException {
        UserEntity user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER, Error.NOT_FOUND_USER.getMessage()));

        return new UserDetailsImpl(user);
    }
}
