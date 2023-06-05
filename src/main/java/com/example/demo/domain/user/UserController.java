package com.example.demo.domain.user;

import com.example.demo.domain.user.dto.SignUpRequest;
import com.example.demo.domain.user.dto.UpdatePasswordRequest;
import com.example.demo.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// TODO : PasswordEncode 추가
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<String> createMember(@RequestBody SignUpRequest signUpRequest) {
        userService.createMember(signUpRequest);
        return ResponseEntity.ok("User created successfully.");
    }

    @PostMapping("/withdrawn")
    public ResponseEntity<String> withdrawMember(@RequestHeader("username") String username, @RequestHeader("password") String password) {
        userService.withdrawMember(username, password);
        return ResponseEntity.ok("User withdrawn successfully.");
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestHeader("username") String username, @RequestHeader("password") String password, @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(username, password, request.getNewpassword());
        return ResponseEntity.ok("Password updated successfully.");
    }
}
