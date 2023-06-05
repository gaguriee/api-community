package com.example.demo.config;

import com.example.demo.domain.Auth.jwt.JwtAccessDeniedHandler;
import com.example.demo.domain.Auth.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.domain.Auth.jwt.TokenProvider;
import com.example.demo.domain.Auth.jwt.filter.JwtFilter;
import com.example.demo.domain.Auth.jwt.filter.RefreshFilter;
import com.example.demo.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity // 웹 보안을 활성화
@EnableGlobalMethodSecurity(prePostEnabled = true) // 메서드 보안을 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RedisTemplate<String, String> redisTemplate;


    // 자동 인터셉트 구현 - url 패턴에 대한 인증 및 인가 처리 자동 수행
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/signup", "/error", "/auth/resetPassword", "/auth/verifyIamport/certification", "/article/public").permitAll()
                        .requestMatchers("/main", "/", "/css/**", "/images/**",
                                "/js/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()

                )
//                .cors().configurationSource(corsConfigurationSource())
                .exceptionHandling(config -> config
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .sessionManagement(config -> config
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Session을 생성하지 않고 Stateless하게 설정
                .addFilterBefore(new JwtFilter(tokenProvider, userRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new RefreshFilter(tokenProvider, redisTemplate), JwtFilter.class); // JwtFilter 다음에 RefreshFilter를 추가

        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}