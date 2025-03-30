package com.example.capstone2.dto;

import com.example.capstone2.domain.LikeType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeDto {

    // 요청용
    private Long userId;           // 좋아요 누른 사람
    private Long boardId;          // 게시글 좋아요일 경우
    private Long likedUserId;      // 사용자 좋아요일 경우

    // 응답용
    private Long id;
    private LocalDateTime likedAt;
}
