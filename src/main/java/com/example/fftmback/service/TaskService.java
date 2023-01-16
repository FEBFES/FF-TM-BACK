package com.example.fftmback.service;

import com.example.fftmback.dto.BoardDto;
import com.example.fftmback.filter.BoardFilter;
import com.example.fftmback.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<BoardDto> getBoards(BoardFilter filter) {
        return Collections.EMPTY_LIST;
    }
}
