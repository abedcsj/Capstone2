package com.example.capstone2.repository;

import com.example.capstone2.domain.BoardParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BoardParticipationRepository extends JpaRepository<BoardParticipation, Long> {
    Optional<BoardParticipation> findByBoardIdAndUserId(Long boardId, Long userId);
    List<BoardParticipation> findByBoardId(Long boardId);
}
