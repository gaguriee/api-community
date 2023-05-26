package com.example.demo.domain.board.mapper;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.dto.BoardResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    BoardResponse entityToResponse(Board board);

    List<BoardResponse> entitiesToListResponse(List<Board> boards);

}