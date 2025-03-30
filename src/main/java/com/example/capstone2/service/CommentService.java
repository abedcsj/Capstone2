package com.example.capstone2.service;

import com.example.capstone2.domain.Board;
import com.example.capstone2.domain.Comment;
import com.example.capstone2.domain.User;
import com.example.capstone2.dto.CommentDto;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.CommentRepository;
import com.example.capstone2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public CommentDto createComment(CommentDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setBoard(board);
        comment.setContent(dto.getContent());

        Comment saved = commentRepository.save(comment);
        return toDto(saved);
    }

    public List<CommentDto> getCommentsByBoardId(Long boardId) {
        return commentRepository.findByBoardId(boardId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .boardId(comment.getBoard().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
