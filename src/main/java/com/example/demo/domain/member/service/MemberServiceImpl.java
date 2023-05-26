package com.example.demo.domain.member.service;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberRepository;
import com.example.demo.domain.member.dto.MemberResponse;
import com.example.demo.domain.member.dto.SignUpRequest;
import com.example.demo.domain.member.mapper.MemberMapper;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(password);

        MemberMapper memberMapper = Mappers.getMapper(MemberMapper.class);
        MemberResponse memberResponse = memberMapper.entityToResponse(member);

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
            Member member = verifyUser(username, password);
            memberRepository.delete(member);
        } catch (NoSuchElementException e) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND, "Invalid username or password.");
        }
    }

    private Member verifyUser(String username, String password) {
        Member member = memberRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND, "User not found."));

        return member;
    }

    public void updatePassword(String username, String password, String newPassword) {
        Member member = verifyUser(username, password);

        // 비밀번호 검증
        if (!isValidPassword(newPassword)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND, "Invalid password. Password must contain uppercase and lowercase letters, and special characters (!, @, #, $, %, ^, &, *) only.");
        }
        member.setPassword(newPassword);
        memberRepository.save(member);

    }
}
