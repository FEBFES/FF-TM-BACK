package com.example.fftmback.service;

import com.example.fftmback.dao.BoardDao;
import com.example.fftmback.domain.Board;
import com.example.fftmback.dto.BoardDto;
import com.example.fftmback.filter.BoardFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final @NonNull BoardDao boardDao;

    public List<BoardDto> getBoards(BoardFilter filter) {
        return boardDao.selectByFilter(filter).stream()
                .map(this::mapBoard)
                .collect(Collectors.toList());
    }

    private BoardDto mapBoard(Board board) {
        return new BoardDto(
                board.getId(),
                board.getName(),
                board.getDateIn()
        );
    }
}
