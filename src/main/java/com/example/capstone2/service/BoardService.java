package com.example.capstone2.service;

import com.example.capstone2.domain.Board;
import com.example.capstone2.domain.Category;
import com.example.capstone2.domain.User;
import com.example.capstone2.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public List<Board>getBoardsByCategory(Category category){
        return boardRepository.findByCategory(category);
    }

    public void deleteBoardByAdmin(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        boardRepository.delete(board);
    }
}

