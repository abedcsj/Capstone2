package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.dto.BoardDto;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        board.setClosed(false); // 기본값: 모집 중
        board.setCreatedAt(LocalDateTime.now()); // 생성 시간 자동 설정

        boardRepository.save(board);
    }

    // 📌 게시글 수정 (본인만 가능)
    @Transactional
    public void updateBoard(Long boardId, BoardDto boardDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getUser().getId().equals(boardDto.getUserId())) {
            throw new IllegalArgumentException("게시판 작성자가 아니므로 수정 권한이 없습니다.");
        }

        board.setTitle(boardDto.getTitle());
        board.setDescription(boardDto.getDescription());
        board.setRequest(boardDto.isRequest());
        board.setCategory(boardDto.getCategory());
        board.setUpdatedAt(LocalDateTime.now()); // 수정 시간 기록

        boardRepository.save(board);
    }

    // 📌 게시글 삭제 (본인만 가능)
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("게시판 작성자가 아니므로 삭제 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }

    // 📌 게시판 글 삭제 (관리자 권한)
    @Transactional
    public void deleteBoardByAdmin(Long boardId, User admin) {
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        boardRepository.delete(board);
    }

    // 📌 게시글 모집 상태 변경 (모집 완료 ↔ 모집 중)
    @Transactional
    public void toggleBoardStatus(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("게시글 작성자만 모집 상태를 변경할 수 있습니다.");
        }

        board.setClosed(!board.isClosed()); // 상태 변경 (모집 중 → 모집 완료 or 모집 완료 → 모집 중)
        boardRepository.save(board);
    }

    // 📌 게시글 카테고리별 조회 (최신순 정렬)
    public List<BoardDto> getBoardsByCategory(Category category) {
        return boardRepository.findByCategory(category).stream()
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt())) // 최신순 정렬
                .map(board -> new BoardDto(
                        board.getId(),
                        board.getUser().getId(),
                        board.getTitle(),
                        board.getDescription(),
                        board.isRequest(),
                        board.getLikeCount(),
                        board.getCategory(),
                        board.isClosed(),
                        board.getCreatedAt(),
                        board.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 📌 게시글 상세 조회
    public BoardDto getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return new BoardDto(
                board.getId(),
                board.getUser().getId(),
                board.getTitle(),
                board.getDescription(),
                board.isRequest(),
                board.getLikeCount(),
                board.getCategory(),
                board.isClosed(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}