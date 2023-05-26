package com.example.demo.domain.board;

import com.example.demo.domain.board.dto.BoardCreateRequest;
import com.example.demo.domain.board.dto.BoardResponse;
import com.example.demo.domain.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


// TODO : 댓글 기능 구현
// TODO : CustomException, GlobalExceptionHandler 만들기

@RestController
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createBoard(@RequestBody BoardCreateRequest boardCreateRequest,
                                              @RequestHeader("username") String username, @RequestHeader("password") String password) {

        boardService.createBoard(username, password, boardCreateRequest);
        return ResponseEntity.ok("Post successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<? extends Object> readBoard(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(boardService.readBoard(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<List<BoardResponse>> readAllBoard() {
        return ResponseEntity.ok(boardService.readAllBoard());
    }


    @GetMapping("/getboard")
    public ResponseEntity<? extends Object> getBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        try {
            return ResponseEntity.ok(boardService.getBoards(page, size));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<? extends Object> updateBoard(@PathVariable Long id,
                                                        @RequestBody BoardCreateRequest boardCreateRequest,
                                                        @RequestHeader("username") String username, @RequestHeader("password") String password) {
        try {
            return ResponseEntity.ok(boardService.updateBoard(username, password, id, boardCreateRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            // TODO : exception 대체
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<? extends Object> deleteBoard(@PathVariable Long id,
                                                        @RequestHeader("username") String username, @RequestHeader("password") String password) {

        try {
            boardService.deleteBoard(id, username, password);
            return ResponseEntity.ok("delete complete!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            // TODO : exception 대체
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
