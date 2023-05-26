package com.example.demo.domain.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponse {
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
