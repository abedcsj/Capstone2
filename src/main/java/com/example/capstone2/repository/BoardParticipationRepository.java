package com.example.capstone2.repository;

import com.example.capstone2.domain.BoardParticipation;
import com.example.capstone2.domain.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardParticipationRepository extends JpaRepository<BoardParticipation, Long> {
    List<BoardParticipation> findByBoardIdAndStatus(Long boardId, ParticipationStatus status);
    Optional<BoardParticipation> findByBoardIdAndUserId(Long boardId, Long userId);

}