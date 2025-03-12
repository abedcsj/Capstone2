package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardParticipationService {
    private final BoardParticipationRepository boardParticipationRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 📌 게시글 참여 신청
    @Transactional
    public void requestParticipation(Long boardId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        BoardParticipation participation = new BoardParticipation();
        participation.setBoard(board);
        participation.setUser(user);
        participation.setApproved(false);
        participation.setServiceCompleted(false);
        boardParticipationRepository.save(participation);
    }

    // 📌 게시글 작성자가 참여 승인
    @Transactional
    public void approveParticipation(Long participationId) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        participation.setApproved(true);
        boardParticipationRepository.save(participation);
    }

    // 📌 서비스 완료 후 참여자가 확인
    @Transactional
    public void completeService(Long participationId) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (!participation.isApproved()) {
            throw new IllegalStateException("아직 게시글 작성자가 승인하지 않았습니다.");
        }

        participation.setServiceCompleted(true);
        boardParticipationRepository.save(participation);
    }

    // 📌 특정 게시글에 참여한 사용자 목록 조회
    public List<BoardParticipationDto> getParticipantsByBoard(Long boardId) {
        return boardParticipationRepository.findByBoardId(boardId).stream()
                .map(part -> new BoardParticipationDto(
                        part.getId(),
                        part.getBoard().getId(),
                        part.getUser().getId(),
                        part.isApproved(),
                        part.isServiceCompleted()))
                .collect(Collectors.toList());
    }
}
