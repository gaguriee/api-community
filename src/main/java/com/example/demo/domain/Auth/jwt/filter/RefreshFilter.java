package com.example.demo.domain.Auth.jwt.filter;

import com.example.demo.domain.Auth.jwt.TokenProvider;
import com.example.demo.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.demo.exception.ErrorCode.INVALID_TOKEN;


@Slf4j
@RequiredArgsConstructor
public class RefreshFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    private final RedisTemplate<String, String> redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {


        if (request.getRequestURI().equals("/auth/tokenRefresh")) {

            // Refresh Token을 검증합니다.
            String refresh_token = tokenProvider.resolveToken(request);

            try {
//             Refresh Token 검증
                if (!tokenProvider.validate(refresh_token)) {
                    throw new CustomException(INVALID_TOKEN, "Invalid refresh token supplied");
                }

                // User phone number를 가져온다.
                Authentication authentication = tokenProvider.resolveFrom(refresh_token);

                // Redis에서 저장된 Refresh Token 값을 가져온다.
                String refreshToken = redisTemplate.opsForValue().get(authentication.getName());
                if (refreshToken == null) {
                    throw new CustomException(INVALID_TOKEN, "Refresh Token not found.");
                } else if (!refreshToken.equals(refresh_token)) {
                    throw new CustomException(INVALID_TOKEN, "Refresh Token doesn't match.");
                }


            } catch (AuthenticationException e) {
                throw new CustomException(INVALID_TOKEN, "Refresh Token doesn't match.");

            }
        }


        chain.doFilter(request, response);

    }

}
