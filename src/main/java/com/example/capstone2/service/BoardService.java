package com.example.capstone2.service;


import com.example.capstone2.domain.Board;
import com.example.capstone2.domain.User;
import com.example.capstone2.dto.BoardDto;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createBoard(Long userId, BoardDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Board board = new Board();
        board.setOwner(user);
        board.setTitle(dto.getTitle());
        board.setDescription(dto.getDescription());
        board.setCreditPrice(dto.getCreditPrice());
        board.setCategory(dto.getCategory());
        board.setClosed(false);

        boardRepository.save(board);
    }


    @Transactional
    public void updateBoard(Long userId, Long boardId, BoardDto dto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!board.getOwner().getId().equals(userId) && !isAdmin()) {
            throw new AccessDeniedException("수정 권한 없음");
        }

        board.setTitle(dto.getTitle());
        board.setDescription(dto.getDescription());
        board.setCreditPrice(dto.getCreditPrice());
        board.setCategory(dto.getCategory());
        board.setClosed(dto.isClosed());
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!board.getOwner().getId().equals(userId) && !isAdmin()) {
            throw new AccessDeniedException("삭제 권한 없음");
        }

        boardRepository.delete(board);
    }
    @Transactional
    public void deleteBoardByAdmin(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        boardRepository.delete(board);
    }

    public BoardDto getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        return toDto(board);
    }

    public List<BoardDto> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void toggleClosed(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!board.getOwner().getId().equals(userId) && !isAdmin()) {
            throw new AccessDeniedException("수정 권한 없음");
        }

        board.setClosed(!board.isClosed()); // ✨ 상태 토글
    }


    private BoardDto toDto(Board board) {
        return new BoardDto(
                board.getId(),
                board.getOwner().getId(),
                board.getTitle(),
                board.getDescription(),
                board.getLikeCount(),
                board.getCategory(),
                board.getCreditPrice(),
                board.isClosed(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
