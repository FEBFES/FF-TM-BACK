package com.example.fftmback.dao;

import com.example.fftmback.domain.Board;
import com.example.fftmback.filter.BoardFilter;

import java.util.List;

public interface BoardDao {

    List<Board> selectByFilter(BoardFilter filter);
}
