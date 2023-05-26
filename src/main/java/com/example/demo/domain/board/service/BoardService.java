package com.example.demo.domain.board.service;

import com.example.demo.domain.board.dto.BoardCreateRequest;
import com.example.demo.domain.board.dto.BoardResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {

    BoardResponse createBoard(String id, String password, BoardCreateRequest boardCreateRequest);

    Page<BoardResponse> getBoards(int page, int size);

    BoardResponse readBoard(Long id);

    List<BoardResponse> readAllBoard();


    BoardResponse updateBoard(String username, String password, Long id, BoardCreateRequest boardCreateRequest);

    void deleteBoard(Long id, String username, String password);
}
