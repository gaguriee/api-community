package com.example.demo.domain.board;

import com.example.demo.domain.board.dto.BoardCreateRequest;
import com.example.demo.domain.board.dto.BoardResponse;
import com.example.demo.domain.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// TODO : 댓글 기능 구현

@RestController
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createBoard(@RequestBody BoardCreateRequest boardCreateRequest) {

        boardService.createBoard(boardCreateRequest);
        return ResponseEntity.ok("Post successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<? extends Object> readBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.readBoard(id));
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
        return ResponseEntity.ok(boardService.getBoards(page, size));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<? extends Object> updateBoard(@PathVariable Long id,
                                                        @RequestBody BoardCreateRequest boardCreateRequest) {
        return ResponseEntity.ok(boardService.updateBoard(id, boardCreateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<? extends Object> deleteBoard(@PathVariable Long id,
                                                        @RequestHeader("username") String username, @RequestHeader("password") String password) {

        boardService.deleteBoard(id);
        return ResponseEntity.ok("delete complete!");
    }


}
