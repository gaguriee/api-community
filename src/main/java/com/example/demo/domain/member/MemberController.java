package com.example.demo.domain.member;

import com.example.demo.domain.member.dto.SignUpRequest;
import com.example.demo.domain.member.dto.UpdatePasswordRequest;
import com.example.demo.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


// TODO : PasswordEncode 추가
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createMember(@RequestBody SignUpRequest signUpRequest) {
        try {
            memberService.createMember(signUpRequest);
            return ResponseEntity.ok("Member created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/withdrawn")
    public ResponseEntity<String> withdrawMember(@RequestHeader("username") String username, @RequestHeader("password") String password) {
        try {
            memberService.withdrawMember(username, password);
            return ResponseEntity.ok("Member withdrawn successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestHeader("username") String username, @RequestHeader("password") String password, @RequestBody UpdatePasswordRequest request) {
        try {
            memberService.updatePassword(username, password, request.getNewpassword());
            return ResponseEntity.ok("Password updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
