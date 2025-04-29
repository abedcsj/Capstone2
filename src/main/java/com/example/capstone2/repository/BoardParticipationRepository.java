package com.example.capstone2.repository;

import com.example.capstone2.domain.BoardParticipation;
import com.example.capstone2.domain.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface BoardParticipationRepository extends JpaRepository<BoardParticipation, Long> {

    // 📌 특정 게시글에 대해 특정 사용자가 참가 신청한 내역 조회
    Optional<BoardParticipation> findByBoardIdAndUserId(Long boardId, Long userId);
    boolean existsByBoardIdAndUserIdAndStatus(Long boardId, Long userId, ParticipationStatus status);
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
    // 특정 boardId에 대한 모든 참여 리스트 조회
    List<BoardParticipation> findByBoardId(Long boardId);

    // 특정 boardId에 대해 APPROVED된 참여 리스트만 조회
    List<BoardParticipation> findByBoardIdAndStatus(Long boardId, ParticipationStatus status);

}
