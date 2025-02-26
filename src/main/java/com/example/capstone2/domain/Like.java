package com.example.capstone2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 좋아요를 누른 사용자

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = true)
    private Board board; // 좋아요 대상 게시글 (게시글 좋아요)

    @ManyToOne
    @JoinColumn(name = "liked_user_id", nullable = true)
    private User likedUser; // 좋아요 대상 사용자 (유저 좋아요)

    private LocalDateTime likedAt;
}
