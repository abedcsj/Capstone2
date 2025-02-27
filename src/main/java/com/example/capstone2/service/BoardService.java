package com.example.capstone2.service;

import com.example.capstone2.domain.Board;
import com.example.capstone2.domain.Category;
import com.example.capstone2.domain.User;
import com.example.capstone2.dto.BoardDto;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 📌 게시글 생성
    public void createBoard(BoardDto boardDto) {
        User user = userRepository.findById(boardDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Board board = new Board();
        board.setUser(user);
        board.setTitle(boardDto.getTitle());
        board.setDescription(boardDto.getDescription());
        board.setRequest(boardDto.isRequest());
        board.setLikeCount(0);
        board.setCategory(boardDto.getCategory());
        boardRepository.save(board);
    }

    // 📌 게시글 수정
    public void updateBoard(Long boardId, BoardDto boardDto) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getUser().getId().equals(boardDto.getUserId())) {
            throw new IllegalArgumentException("게시판 작성자가 아니므로 수정 권한이 없습니다");
        }
        board.setTitle(boardDto.getTitle());
        board.setDescription(boardDto.getDescription());
        board.setRequest(boardDto.isRequest());
        board.setCategory(boardDto.getCategory());

        boardRepository.save(board);
    }
    // 📌 게시글 삭제
    public void deleteBoard(Long boardId, BoardDto boardDto) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getUser().getId().equals(boardDto.getUserId())) {
            throw new IllegalArgumentException("게시판 작성자가 아니므로 수정 권한이 없습니다");
        }
        boardRepository.delete(board);
    }

    // 📌 해당 카테고리의 게시글 목록 조회
    public List<BoardDto> getBoardsByCategory(Category category) {
        List<Board> boards = boardRepository.findByCategory(category);
        return boards.stream()
                .map(board -> new BoardDto(
                        board.getId(),
                        board.getUser().getId(),
                        board.getTitle(),
                        board.getDescription(),
                        board.isRequest(),
                        board.getLikeCount(),
                        board.getCategory()
                ))
                .collect(Collectors.toList());
    }


    // 📌 게시글 삭제(게시글 작성자가 누구든 상관없이 모두 삭제 가능) | (관리자 기능)
    public void deleteBoardByAdmin(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        boardRepository.delete(board);
    }

}

