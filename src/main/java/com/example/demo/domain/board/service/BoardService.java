package com.example.demo.domain.board.service;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.dto.BoardCreateRequest;
import com.example.demo.domain.member.Member;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {

    Board createBoard(String id, String password, BoardCreateRequest boardCreateRequest);

    Member verityUser(String id, String password);

    Page<Board> getBoards(int page, int size);

    long getTotalBoardCount();

    Board readBoard(Long id);

    List<Board> readAllBoard();


    Board updateBoard(String username, String password, Long id, BoardCreateRequest boardCreateRequest);

    void deleteBoard(Long id, String username, String password);
}
