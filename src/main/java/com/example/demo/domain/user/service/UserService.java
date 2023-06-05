package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.MemberResponse;
import com.example.demo.domain.user.dto.SignUpRequest;

public interface UserService {

    MemberResponse createMember(SignUpRequest signUpRequest);

    void withdrawMember(String username, String password);

    void updatePassword(String username, String password, String newPassword);
}
