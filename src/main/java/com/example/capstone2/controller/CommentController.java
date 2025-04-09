package com.example.capstone2.controller;

import com.example.capstone2.dto.CommentDto;
import com.example.capstone2.security.PrincipalDetails;
import com.example.capstone2.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto dto,
                                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        CommentDto created = commentService.createComment(dto, userId);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long boardId,
                                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        List<CommentDto> comments = commentService.getCommentsByBoardId(boardId, userId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
