package com.example.demo.domain.member.service;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.dto.SignUpRequest;

public interface MemberService {

    Member createMember(SignUpRequest signUpRequest);

    void withdrawMember(String username, String password);

    Member verifyUser(String username, String password);

    void updatePassword(String username, String password, String newPassword);
}
