package com.example.demo.domain.board.service;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.BoardRepository;
import com.example.demo.domain.board.dto.BoardCreateRequest;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberRepository;
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

    public Board createBoard(String id, String password, BoardCreateRequest boardCreateRequest) {

        // 사용자 verify
        Member member = verityUser(id, password);

        // 게시글 등록 로직 구현
        Board board = new Board();
        board.setAuthor(member);
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());

        return boardRepository.save(board);
    }

    public Member verityUser(String id, String password) {
        // 게시글 등록 로직 구현

        return memberRepository.findByUsernameAndPassword(id, password).get();
    }

    public Page<Board> getBoards(int page, int size) {
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
        return boardRepository.findAll(pageable);
    }

    public long getTotalBoardCount() {
        return boardRepository.count();
    }

    // 단일 게시물 하나 읽기
    public Board readBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("no such elements with id : " + id));
    }

    // 전체 게시물 읽기
    public List<Board> readAllBoard() {

        return boardRepository.findAll();
    }


    public Board updateBoard(String username, String password, Long id, BoardCreateRequest boardCreateRequest) {

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
        return boardRepository.save(existingBoard);
    }

    public void deleteBoard(Long id, String username, String password) {

        // 사용자 verify
        Member member = verityUser(username, password);

        // 게시글 verify
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board id: " + id));

        if (member.getId() != board.getAuthor().getId()) {
            throw new RuntimeException(); // TODO : CustomException으로 변경
        }

        // 게시글 삭제 로직 구현
        boardRepository.deleteById(id);
    }
}
