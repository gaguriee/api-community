package com.example.demo.domain.Auth.jwt.filter;


import com.example.demo.domain.Auth.jwt.TokenProvider;
import com.example.demo.domain.user.UserRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.demo.exception.ErrorCode.INVALID_TOKEN;


@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // 토큰의 인증 정보를 Security Context에 저장하는 역할 수행

        // 현재 요청과 관련된 고유한 식별자
        String traceId = (String) request.getAttribute("traceId");

        try {
            String jwt = tokenProvider.resolveToken(request);
            Integer userId = 0;

            tokenProvider.validate(jwt);
            if (jwt != null) {
                Authentication authentication = tokenProvider.resolveFrom(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("valid authentication: {}, uri: {}", authentication,
                        request.getRequestURI());

                if (authentication == null) {
                    log.info("no authentication info found");
                }

                Object principal = authentication.getPrincipal(); // 사용자의 식별 정보를 추출
                if (principal instanceof UserDetails) {
                    User user = userRepository.findByUsername(authentication.getName())
                            .orElseThrow(
                                    () -> new CustomException(INVALID_TOKEN, String.format("'%s' not found", authentication.getName())));
                    userId = user.getId();

                }


            } else {
                throw new CustomException(INVALID_TOKEN, "token is invalid in jwt filter");
            }

            // set userId in request
            request.setAttribute("id", userId);
        } catch (CustomException e) {
            log.info("trace id " + traceId + " has a jwt filter error, " + e.getValue());
        } catch (Exception e) {
            log.info("trace id " + traceId + " has a jwt filter error, " + e.getMessage());
        }

        chain.doFilter(request, response);

    }


}
