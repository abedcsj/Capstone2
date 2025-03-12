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
public class BoardParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 📌 참가 신청한 게시글
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 📌 참가 신청한 사용자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 📌 참가 상태 (PENDING, APPROVED, REJECTED, COMPLETED)
    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    // 📌 참가 요청 시간
    private LocalDateTime requestedAt;

    // 📌 승인 시간
    private LocalDateTime approvedAt;

    @PrePersist
    public void prePersist() {
        this.requestedAt = LocalDateTime.now();
    }
}