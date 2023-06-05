package com.example.demo.domain.user.dto;

import lombok.Data;


@Data
public class SignUpRequest {
    private String username;
    private String password;
}
