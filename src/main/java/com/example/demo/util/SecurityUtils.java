package com.example.demo.util;

import com.example.demo.domain.user.UserRepository;
import com.example.demo.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/*
 Spring Security와 함께 사용할 때 현재 사용자의 이름을 가져오는 유틸리티 클래스인 SecurityUtils를 정의
 */

@Slf4j
@Component
public class SecurityUtils {


    public static Optional<String> getCurrentUsername() {

        // Spring Security의 SecurityContextHolder 클래스를 사용하여 현재 인증 된 사용자의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.info("no authentication info found");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal(); // 사용자의 식별 정보를 추출
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return Optional.ofNullable(userDetails.getUsername());
        }
        if (principal instanceof String) {
            return Optional.of(principal.toString());
        }

        throw new IllegalStateException("invalid authentication");
    }

    public static Optional<Integer> getCurrentUserId(UserRepository userRepository) {
        Optional<String> currentUsername = getCurrentUsername();
        if (!currentUsername.isPresent()) {
            return Optional.empty();
        }
        Optional<User> user = userRepository.findByUsername(currentUsername.get());
        if (user.isPresent()) {
            return Optional.ofNullable(user.get().getId());
        } else {
            return Optional.empty();
        }
    }
}
