package com.example.demo.config;


import com.example.demo.domain.user.UserRepository;
import com.example.demo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/*
 Spring Security에서 사용자 인증을 위해 구현해야 하는 UserDetailsService의 구현체
 */

@Service("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService { // UserDetailsService - Spring Security에서 사용자 인증 시 필요한 인터페이스

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 휴대폰번호(phone_number)을 받아 해당 사용자 정보를 데이터베이스에서 찾아 인증 정보를 제공하는 역할

        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format("'%s' not found", username)));

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toSet()));
    }

}