package com.example.capstone2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private Long reviewerId;
    private Long revieweeId;
    private Long boardId;      // 게시판 ID
    private String content;
    private int rating;
    private LocalDateTime createdAt;
}
