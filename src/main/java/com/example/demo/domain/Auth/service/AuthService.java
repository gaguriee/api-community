package com.example.demo.domain.Auth.service;


import com.example.demo.domain.Auth.dto.SignupRequest;
import com.example.demo.domain.Auth.dto.TokenResponse;
import com.example.demo.domain.Auth.jwt.TokenProvider;
import com.example.demo.domain.Auth.mapper.SignupMapper;
import com.example.demo.domain.user.UserRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.example.demo.exception.ErrorCode.*;

/*
 * JWT 토큰을 이용한 인증 방식에서 사용자 정보를 처리하는 서비스 클래스인 AuthService
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 작업만 수행하고 데이터를 변경하지 않음
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${jwt.refresh_expired-time}")
    long refreshExpired;

    // TODO : 인터페이스 분리하기

    @Transactional(readOnly = false)
    public void signup(SignupRequest signupRequest) throws CustomException {
        try {
            User userInDb = userRepository.findByUsername(signupRequest.getUsername())
                    .orElse(null);


            if (userInDb != null && userInDb.getActivated() == true) {

                throw new CustomException(DUPLICATED_MEMBER, "User already exists with phone number : " + signupRequest.getUsername());
            }

            signupRequest.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

            User user = SignupMapper.INSTANCE.toEntity(signupRequest);

            userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            throw new CustomException(SERVER_ERROR, "Unexpected error occurred during user signup");
        }


    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // Redis 저장된 토큰 삭제
            redisTemplate.opsForValue().getAndDelete(authentication.getName());
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }


    @Transactional(readOnly = false)
    public TokenResponse login(String phone, String password) throws CustomException {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phone, password);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);


            // 접속 시간 업데이트

            User userInDb = userRepository.findById(SecurityUtils.getCurrentUserId(userRepository).get())
                    .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND, "User Not Exists with Id : " + SecurityUtils.getCurrentUserId(userRepository).get()));

            userInDb.setLastAccessed(LocalDateTime.now());

            userRepository.save(userInDb);

            // Redis에 저장 - 만료 시간 설정을 통해 자동 삭제 처리

            TokenResponse tokenResponse = tokenProvider.createFrom(authentication);

            redisTemplate.opsForValue().set( // void set(K key, V value, long timeout, TimeUnit unit);
                    authentication.getName(),
                    tokenResponse.getRefreshToken(),
                    refreshExpired,
                    TimeUnit.SECONDS
            );

            return tokenResponse;
        } catch (BadCredentialsException e) {
            throw new CustomException(INVALID_AUTHORITY, "Invalid id or password");
        }
    }


    public TokenResponse refreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 토큰 재발행
        TokenResponse jwt = tokenProvider.createFrom(authentication);

        // RefreshToken Redis에 업데이트
        redisTemplate.opsForValue().set(
                authentication.getName(),
                jwt.getRefreshToken(),
                refreshExpired,
                TimeUnit.SECONDS
        );
        return jwt;
    }


}
