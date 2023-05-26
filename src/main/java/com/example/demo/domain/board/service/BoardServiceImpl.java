package com.example.demo.domain.board.service;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.BoardRepository;
import com.example.demo.domain.board.dto.BoardCreateRequest;
import com.example.demo.domain.board.dto.BoardResponse;
import com.example.demo.domain.board.mapper.BoardMapper;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository, MemberRepository memberRepository) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
    }

    public BoardResponse createBoard(String id, String password, BoardCreateRequest boardCreateRequest) {

        // 사용자 verify
        Member member = verityUser(id, password);

        // 게시글 등록 로직 구현
        Board board = new Board();
        board.setAuthor(member);
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());

        BoardMapper boardMapper = Mappers.getMapper(BoardMapper.class);
        BoardResponse boardResponse = boardMapper.entityToResponse(boardRepository.save(board));

        return boardResponse;
    }

    private Member verityUser(String id, String password) {
        // 게시글 등록 로직 구현

        return memberRepository.findByUsernameAndPassword(id, password).get();
    }

    public Page<BoardResponse> getBoards(int page, int size) {
        // 페이지당 게시물 갯수 범위 검증
        if (size < 5 || size > 10) {
            throw new IllegalArgumentException("Invalid size. Size must be between 5 and 10.");
        }

        long totalCount = getTotalBoardCount();
        long totalPages = (totalCount + size - 1) / size;

        // Offset 값 검증
        if (page < 0 || page >= totalPages) {
            throw new IllegalArgumentException("Invalid page. Page is out of range.");
        }

        System.out.println("totalCount : " + totalCount);
        Pageable pageable = PageRequest.of(page, size);
        System.out.println("pageable : " + pageable);

        BoardMapper boardMapper = Mappers.getMapper(BoardMapper.class);
        Page<BoardResponse> boardResponse = boardMapper.entitiesTPageResponse(boardRepository.findAll(pageable));

        return boardResponse;
    }

    public long getTotalBoardCount() {
        return boardRepository.count();
    }

    // 단일 게시물 하나 읽기
    public BoardResponse readBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("no such elements with id : " + id));

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


    public BoardResponse updateBoard(String username, String password, Long id, BoardCreateRequest boardCreateRequest) {

        // 사용자 verify
        Member member = verityUser(username, password);

        // 게시글 verify
        Board board = boardRepository.findById(id).get();

        // 게시글 수정 로직 구현

        if (member.getId() != board.getAuthor().getId()) {
            // TODO : CustomException으로 바꾸기
            throw new RuntimeException("invalid authority");
        }

        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board id: " + id));
        existingBoard.setTitle(boardCreateRequest.getTitle());
        existingBoard.setContent(boardCreateRequest.getContent());

        BoardMapper boardMapper = Mappers.getMapper(BoardMapper.class);
        BoardResponse boardResponse = boardMapper.entityToResponse(boardRepository.save(existingBoard));
        return boardResponse;
    }

    public void deleteBoard(Long id, String username, String password) {

        // 사용자 verify
        Member member = verityUser(username, password);

        // 게시글 verify
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board id: " + id));

        if (member.getId() != board.getAuthor().getId()) {
            throw new RuntimeException("invalid authority"); // TODO : CustomException으로 변경
        }

        // 게시글 삭제 로직 구현
        boardRepository.deleteById(id);
    }
}
