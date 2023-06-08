package com.example.demo.domain.board.service;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.BoardRepository;
import com.example.demo.domain.board.dto.BoardCreateRequest;
import com.example.demo.domain.board.dto.BoardResponse;
import com.example.demo.domain.board.mapper.BoardMapper;
import com.example.demo.domain.user.UserRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.util.SecurityUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.exception.ErrorCode.*;

@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public BoardResponse createBoard(BoardCreateRequest boardCreateRequest) {

        // 사용자 verify
        User user = verityUser();

        // 게시글 등록 로직 구현
        Board board = new Board();
        board.setAuthor(user);
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());

        BoardMapper boardMapper = Mappers.getMapper(BoardMapper.class);
        BoardResponse boardResponse = boardMapper.entityToResponse(boardRepository.save(board));

        return boardResponse;
    }

    public Page<Board> getBoards(int page, int size) {
        // 페이지당 게시물 갯수 범위 검증

        long totalCount = getTotalBoardCount();
        long totalPages = (totalCount + size - 1) / size;

        // Offset 값 검증
        if (page < 0 || page >= totalPages) {
            throw new CustomException(ENTITY_NOT_FOUND, "offset is out of bound");
        }

        Pageable pageable = PageRequest.of(page, size);

        return boardRepository.findAll(pageable);
    }

    public long getTotalBoardCount() {
        return boardRepository.count();
    }

    // 단일 게시물 하나 읽기
    public BoardResponse readBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ENTITY_NOT_FOUND, "no such elements with id : " + id));

        BoardMapper boardMapper = Mappers.getMapper(BoardMapper.class);
        BoardResponse boardResponse = boardMapper.entityToResponse(board);

        return boardResponse;
    }

    // 전체 게시물 읽기
    public List<BoardResponse> readAllBoard() {

        BoardMapper boardMapper = Mappers.getMapper(BoardMapper.class);
        List<BoardResponse> boardResponse = boardMapper.entitiesToListResponse(boardRepository.findAll());

        return boardResponse;
    }


    public BoardResponse updateBoard(Long id, BoardCreateRequest boardCreateRequest) {

        // 사용자 verify
        User user = verityUser();

        // 게시글 verify
        Board board = boardRepository.findById(id).get();

        // 게시글 수정 로직 구현

        if (user.getId() != board.getAuthor().getId()) {
            throw new CustomException(INVALID_AUTHORITY, "not the owner of the post");
        }

        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ENTITY_NOT_FOUND, "no such elements with id : " + id));
        existingBoard.setTitle(boardCreateRequest.getTitle());
        existingBoard.setContent(boardCreateRequest.getContent());

        BoardMapper boardMapper = Mappers.getMapper(BoardMapper.class);
        BoardResponse boardResponse = boardMapper.entityToResponse(boardRepository.save(existingBoard));
        return boardResponse;
    }

    public void deleteBoard(Long id) {

        // 사용자 verify
        User user = verityUser();

        // 게시글 verify
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ENTITY_NOT_FOUND, "no such elements with id : " + id));

        if (user.getId() != board.getAuthor().getId()) {
            throw new CustomException(INVALID_AUTHORITY, "not the owner of the post");
        }

        // 게시글 삭제 로직 구현
        boardRepository.deleteById(id);
    }

    private User verityUser() {
        // 게시글 등록 로직 구현

        return userRepository.findById(SecurityUtils.getCurrentUserId(userRepository).get())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND, "invalid id or password"));
    }
}
