package com.example.demo.domain.Auth.jwt;

import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 유효한 자격 증명을 거치지 않고 접근할 때 401 에러를 리턴할 클래스

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        throw new CustomException(ErrorCode.INVALID_TOKEN, "no valid credentials");

    }
}
