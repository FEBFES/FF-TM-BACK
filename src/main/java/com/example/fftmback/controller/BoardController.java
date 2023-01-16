package com.example.fftmback.controller;

import com.example.fftmback.dto.BoardDto;
import com.example.fftmback.filter.BoardFilter;
import com.example.fftmback.service.TaskService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final @NonNull TaskService taskService;

    @GetMapping
    public List<BoardDto> getBoards(BoardFilter filter) {
        return taskService.getBoards(filter);
    }
}
