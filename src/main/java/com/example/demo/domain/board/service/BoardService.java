package com.example.demo.domain.board.service;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.dto.BoardCreateRequest;
import com.example.demo.domain.board.dto.BoardResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {

    BoardResponse createBoard(BoardCreateRequest boardCreateRequest);

    // TODO: Dto 매핑하기
    Page<Board> getBoards(int page, int size);

    BoardResponse readBoard(Long id);

    List<BoardResponse> readAllBoard();


    BoardResponse updateBoard(Long id, BoardCreateRequest boardCreateRequest);

    void deleteBoard(Long id);
}
