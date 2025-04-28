package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.dto.CommentDto;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.CommentRepository;
import com.example.capstone2.repository.UserRepository;
import com.example.capstone2.security.PrincipalDetails;
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
    private final BoardParticipationRepository boardParticipationRepository;

    // 댓글 생성
    @Transactional
    public CommentDto createComment(CommentDto dto, Long userId) {
        User user = userRepository.findById(userId)
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

    public List<CommentDto> getCommentsByBoardId(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        boolean isOwner = board.getOwner().getId().equals(userId);
        boolean isParticipant = boardParticipationRepository.existsByBoardIdAndUserId(boardId, userId);

        if (!isOwner && !isParticipant) {
            throw new SecurityException("해당 게시글의 작성자 또는 참여자만 댓글을 볼 수 있습니다.");
        }
        return commentRepository.findByBoardId(boardId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    // 댓글 삭제 (작성자 본인만 가능)
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("본인만 댓글을 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
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
