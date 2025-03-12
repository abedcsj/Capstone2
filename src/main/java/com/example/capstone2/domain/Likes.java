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
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 📌좋아요를 누른 사용자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // 📌게시글 좋아요
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = true)
    private Board board;
    // 📌사용자 좋아요
    @ManyToOne
    @JoinColumn(name = "liked_user_id", nullable = true)
    private User likedUser;
    // 📌좋아요 타입 (게시글, 사용자)
    @Enumerated(EnumType.STRING)
    private LikeType type;

    private LocalDateTime likedAt;

    @PrePersist
    public void prePersist() {
        this.likedAt = LocalDateTime.now();
    }
}
