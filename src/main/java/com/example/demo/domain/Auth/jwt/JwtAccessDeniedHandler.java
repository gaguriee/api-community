package com.example.demo.domain.Auth.jwt;


import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // 필요한 권한이 존재하지 않을 경우 403 Forbidden 에러를 리턴할 클래스

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        throw new CustomException(ErrorCode.INVALID_AUTHORITY, "Required privilege does not exist.");
    }
}
