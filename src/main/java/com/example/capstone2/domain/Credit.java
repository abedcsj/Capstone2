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
    public class Credit {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // 📌크레딧을 보낸 사람
        @ManyToOne
        @JoinColumn(name = "from_user_id", nullable = false)
        private User fromUser;

        // 📌크레딧을 받은 사람
        @ManyToOne
        @JoinColumn(name = "to_user_id", nullable = false)
        private User toUser;

        // 📌전송된 크레딧 수량
        private int amount;

        @ManyToOne
        @JoinColumn(name = "board_id")
        private Board board;

        // 📌크레딧 유형 (SEND, RECEIVE)
        @Enumerated(EnumType.STRING)
        private CreditType type;

        // 📌크레딧 상태 (PENDING, COMPLETED)
        @Enumerated(EnumType.STRING)
        private CreditStatus status;

        // 📌 크레딧이 보류되는 DB (중간 관리자)
        @Column(nullable = false)
        private boolean isHeldInEscrow = true;

        private LocalDateTime transactionTime;

        @PrePersist
        public void prePersist() {
            this.transactionTime = LocalDateTime.now();
        }
    }
