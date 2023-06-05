package com.example.demo.domain.Auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotNull
    @Size(min = 10, max = 11)
    private String username;

    @NotNull
    private String password;
}
