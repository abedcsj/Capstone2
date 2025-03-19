package com.example.capstone2.repository;

import com.example.capstone2.domain.BoardParticipation;
import com.example.capstone2.domain.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardParticipationRepository extends JpaRepository<BoardParticipation, Long> {

    // 📌 특정 게시글에 대해 특정 상태(PENDING, APPROVED 등)인 참가 신청 목록 조회
    List<BoardParticipation> findByBoardIdAndStatus(Long boardId, ParticipationStatus status);

    // 📌 특정 게시글에 대해 특정 사용자가 참가 신청한 내역 조회
    Optional<BoardParticipation> findByBoardIdAndUserId(Long boardId, Long userId);

    // 📌 특정 사용자의 모든 참가 신청 내역 조회
    List<BoardParticipation> findByUserId(Long userId);

    // 📌 특정 사용자의 환불된 참가 신청 내역 조회
    List<BoardParticipation> findByUserIdAndStatus(Long userId, ParticipationStatus status);
}
