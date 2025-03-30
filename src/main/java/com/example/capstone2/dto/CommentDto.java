package com.example.capstone2.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    // 요청용
    private Long userId;
    private Long boardId;
    private String content;

    // 응답용
    private Long id;
    private LocalDateTime createdAt;
}
