package com.example.demo.domain.Auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupRequest {
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @JsonProperty(access = Access.WRITE_ONLY)
    @NotNull
    private String password;

    @Size(min = 10, max = 255)
    private String uri;


}