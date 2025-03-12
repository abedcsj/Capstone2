package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardParticipationService {
    private final BoardParticipationRepository boardParticipationRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 📌 게시글 참여 신청 (PENDING 상태로 저장)
    @Transactional
    public void requestParticipation(Long boardId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        BoardParticipation participation = new BoardParticipation();
        participation.setBoard(board);
        participation.setUser(user);
        participation.setStatus(ParticipationStatus.PENDING);
        participation.setRequestedAt(LocalDateTime.now());
        boardParticipationRepository.save(participation);
    }

    // 📌 게시글 작성자가 참여 승인 (PENDING → APPROVED)
    @Transactional
    public void approveParticipation(Long participationId) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        participation.setStatus(ParticipationStatus.APPROVED);
        participation.setApprovedAt(LocalDateTime.now());
        boardParticipationRepository.save(participation);
    }

    // 📌 게시글 작성자가 참여 거절 (PENDING → REJECTED)
    @Transactional
    public void rejectParticipation(Long participationId) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        participation.setStatus(ParticipationStatus.REJECTED);
        boardParticipationRepository.save(participation);
    }

    // 📌 서비스 완료 후 참여자가 확인 (크레딧 지급)
    @Transactional
    public void completeService(Long participationId) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (participation.getStatus() != ParticipationStatus.APPROVED) {
            throw new IllegalStateException("아직 게시글 작성자가 승인하지 않았습니다.");
        }

        participation.setStatus(ParticipationStatus.COMPLETED);
        boardParticipationRepository.save(participation);
    }

    // 📌 게시글 참여 후 서비스 진행 (APPROVED 상태 → COMPLETED)
    @Transactional
    public void joinBoard(Long boardId, Long userId) {
        BoardParticipation participation = boardParticipationRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new IllegalArgumentException("참여 신청이 존재하지 않습니다."));

        if (participation.getStatus() != ParticipationStatus.APPROVED) {
            throw new IllegalStateException("참여 신청이 승인되지 않았습니다.");
        }

        participation.setStatus(ParticipationStatus.COMPLETED);
        boardParticipationRepository.save(participation);
    }

    // 📌 특정 게시글에 참여한 사용자 목록 조회 (APPROVED 상태만)
    public List<BoardParticipationDto> getParticipantsByBoard(Long boardId) {
        return boardParticipationRepository.findByBoardIdAndStatus(boardId, ParticipationStatus.APPROVED).stream()
                .map(part -> new BoardParticipationDto(
                        part.getId(),
                        part.getBoard().getId(),
                        part.getUser().getId(),
                        part.getStatus(),
                        part.getRequestedAt(),
                        part.getApprovedAt()))
                .collect(Collectors.toList());
    }
}
