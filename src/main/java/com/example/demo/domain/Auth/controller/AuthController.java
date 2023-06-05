package com.example.demo.domain.Auth.controller;


import com.example.demo.domain.Auth.dto.LoginRequest;
import com.example.demo.domain.Auth.dto.SignupRequest;
import com.example.demo.domain.Auth.dto.TokenResponse;
import com.example.demo.domain.Auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return "signup success";
    }

    // 로그인
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return token;
    }


    // 로그아웃
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return "logout success";
    }


    // accessToken 재발급
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/tokenRefresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenResponse refresh() {
        return authService.refreshToken();
    }


}
