package com.example.demo.domain.board.dto;


import lombok.Data;


@Data
public class BoardCreateRequest {

    private String title;

    private String content;

}
