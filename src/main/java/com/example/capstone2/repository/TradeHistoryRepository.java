package com.example.capstone2.repository;

import com.example.capstone2.domain.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {
    // 기록 거래 확인
    boolean existsByUser1IdAndUser2IdAndIsCompleted(Long user1Id, Long user2Id, boolean isCompleted);
}
