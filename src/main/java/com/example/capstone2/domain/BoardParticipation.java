package com.example.capstone2.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BoardParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 📌 참여 승인 여부
    private boolean approved = false;

    // 📌 서비스 완료 여부 (참여자가 확인해야 크레딧 지급)
    private boolean serviceCompleted = false;
}
