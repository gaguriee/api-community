package com.example.demo.domain.member.service;

import com.example.demo.domain.member.dto.MemberResponse;
import com.example.demo.domain.member.dto.SignUpRequest;

public interface MemberService {

    MemberResponse createMember(SignUpRequest signUpRequest);

    void withdrawMember(String username, String password);

    void updatePassword(String username, String password, String newPassword);
}
