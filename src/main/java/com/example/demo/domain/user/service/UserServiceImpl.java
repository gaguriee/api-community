package com.example.demo.domain.user.service;

import com.example.demo.domain.user.UserRepository;
import com.example.demo.domain.user.dto.MemberResponse;
import com.example.demo.domain.user.dto.SignUpRequest;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.mapper.UserMapper;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MemberResponse createMember(SignUpRequest signUpRequest) {
        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();

        // 아이디 검증
        if (!isValidUsername(username)) {
            throw new CustomException(ErrorCode.MISMATCH_INFO, "Invalid username. Username must start with an English letter and contain no special characters.");
        }

        // 비밀번호 검증
        if (!isValidPassword(password)) {
            throw new CustomException(ErrorCode.MISMATCH_INFO, "Invalid password. Password must contain uppercase and lowercase letters, and special characters (!, @, #, $, %, ^, &, *) only.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        UserMapper userMapper = Mappers.getMapper(UserMapper.class);
        MemberResponse memberResponse = userMapper.entityToResponse(user);

        return memberResponse;
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z][a-zA-Z0-9]{0,19}$");
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$");
    }

    public void withdrawMember(String username, String password) {
        try {
            User user = verifyUser(username, password);
            userRepository.delete(user);
        } catch (NoSuchElementException e) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND, "Invalid username or password.");
        }
    }

    private User verifyUser(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND, "User not found."));

        return user;
    }

    public void updatePassword(String username, String password, String newPassword) {
        User user = verifyUser(username, password);

        // 비밀번호 검증
        if (!isValidPassword(newPassword)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND, "Invalid password. Password must contain uppercase and lowercase letters, and special characters (!, @, #, $, %, ^, &, *) only.");
        }
        user.setPassword(newPassword);
        userRepository.save(user);

    }
}
