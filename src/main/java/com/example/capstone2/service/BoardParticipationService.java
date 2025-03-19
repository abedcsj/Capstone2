package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.UserRepository;
import com.example.capstone2.repository.CreditRepository;
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
    private final CreditRepository creditRepository;

    // 📌 게시글 참여 신청 (PENDING 상태 & 크레딧 결제)
    @Transactional
    public void requestParticipation(Long boardId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        User owner = board.getOwner();

        if (owner == null) {
            throw new IllegalStateException("게시글의 소유자가 존재하지 않습니다.");
        }

        // 참가 요청 생성
        BoardParticipation participation = new BoardParticipation();
        participation.setBoard(board);
        participation.setUser(user);
        participation.setStatus(ParticipationStatus.PENDING);
        participation.setRequestedAt(LocalDateTime.now());

        // 📌 참가자가 크레딧을 보냄 (SEND)
        Credit sendCredit = new Credit();
        sendCredit.setFromUser(user);
        sendCredit.setToUser(owner);
        sendCredit.setAmount(1);
        sendCredit.setType(CreditType.SEND);
        sendCredit.setStatus(CreditStatus.PENDING);
        sendCredit.setTransactionTime(LocalDateTime.now());

        // 📌 게시글 작성자가 크레딧을 받음 (RECEIVE)
        Credit receiveCredit = new Credit();
        receiveCredit.setFromUser(user);
        receiveCredit.setToUser(owner);
        receiveCredit.setAmount(1);
        receiveCredit.setType(CreditType.RECEIVE);
        receiveCredit.setStatus(CreditStatus.PENDING);
        receiveCredit.setTransactionTime(LocalDateTime.now());

        // 크레딧 저장
        creditRepository.save(sendCredit);
        creditRepository.save(receiveCredit);

        // 참가 신청과 크레딧 연결
        participation.setCredit(sendCredit);
        boardParticipationRepository.save(participation);
    }

    // 📌 게시글 작성자가 참여 승인 (PENDING → APPROVED, 크레딧 즉시 이전)
    @Transactional
    public void approveParticipation(Long participationId) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (participation.getStatus() != ParticipationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 참가 요청입니다.");
        }

        // 참가 승인 처리
        participation.setStatus(ParticipationStatus.APPROVED);
        participation.setApprovedAt(LocalDateTime.now());

        // 📌 크레딧 즉시 이체 (SEND & RECEIVE → COMPLETED)
        List<Credit> credits = creditRepository.findAll();
        for (Credit credit : credits) {
            if (credit.getFromUser().equals(participation.getUser()) && credit.getToUser().equals(participation.getBoard().getOwner())) {
                credit.setStatus(CreditStatus.COMPLETED);
                creditRepository.save(credit);
            }
        }

        boardParticipationRepository.save(participation);
    }

    // 📌 게시글 작성자가 참여 거절 (PENDING → REJECTED, 크레딧 환불)
    @Transactional
    public void rejectParticipation(Long participationId) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (participation.getStatus() != ParticipationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 참가 요청입니다.");
        }

        // 참가 거부 처리
        participation.setStatus(ParticipationStatus.REJECTED);

        // 📌 크레딧 환불 (SEND & RECEIVE → REFUNDED)
        List<Credit> credits = creditRepository.findAll();
        for (Credit credit : credits) {
            if (credit.getFromUser().equals(participation.getUser()) && credit.getToUser().equals(participation.getBoard().getOwner())) {
                credit.setStatus(CreditStatus.REFUNDED);
                creditRepository.save(credit);
            }
        }

        boardParticipationRepository.save(participation);
    }

    // 📌 참가자가 환불 요청 (APPROVED → REFUNDED, 크레딧 반환)
    @Transactional
    public void requestRefund(Long participationId) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (participation.getStatus() != ParticipationStatus.APPROVED) {
            throw new IllegalStateException("승인된 참가 요청만 환불할 수 있습니다.");
        }

        // 환불 상태 변경
        participation.setStatus(ParticipationStatus.REFUNDED);

        // 📌 크레딧 환불 (SEND & RECEIVE → REFUNDED)
        List<Credit> credits = creditRepository.findAll();
        for (Credit credit : credits) {
            if (credit.getFromUser().equals(participation.getUser()) && credit.getToUser().equals(participation.getBoard().getOwner())) {
                credit.setStatus(CreditStatus.REFUNDED);
                creditRepository.save(credit);
            }
        }

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
                        part.getApprovedAt(),
                        part.getCredit().getId(),
                        part.getCredit().getAmount()))
                .collect(Collectors.toList());
    }
}
