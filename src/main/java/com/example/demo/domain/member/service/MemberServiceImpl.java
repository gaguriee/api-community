package com.example.demo.domain.member.service;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberRepository;
import com.example.demo.domain.member.dto.SignUpRequest;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(SignUpRequest signUpRequest) {
        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();

        // 아이디 검증
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username. Username must start with an English letter and contain no special characters.");
        }

        // 비밀번호 검증
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid password. Password must contain uppercase and lowercase letters, and special characters (!, @, #, $, %, ^, &, *) only.");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(password);

        return memberRepository.save(member);
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
            throw new IllegalArgumentException("Invalid username or password.");
        }
    }

    public Member verifyUser(String username, String password) {
        Optional<Member> optionalMember = memberRepository.findByUsernameAndPassword(username, password);

        if (optionalMember.isPresent()) {
            return optionalMember.get();
        } else {
            throw new NoSuchElementException("User not found.");
        }
    }

    public void updatePassword(String username, String password, String newPassword) {
        Member member = verifyUser(username, password);

        // 비밀번호 검증
        if (!isValidPassword(newPassword)) {
            throw new IllegalArgumentException("Invalid password. Password must contain uppercase and lowercase letters, and special characters (!, @, #, $, %, ^, &, *) only.");
        }
        member.setPassword(newPassword);
        memberRepository.save(member);

    }
}
