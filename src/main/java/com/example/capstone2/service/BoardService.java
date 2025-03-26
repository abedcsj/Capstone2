package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.dto.BoardDto;
import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final CreditRepository creditRepository;
    private final BoardParticipationRepository boardParticipationRepository;

    // 📌 게시글 생성
    public void createBoard(BoardDto boardDto, User authenticatedUser) {
        Board board = new Board();
        board.setOwner(authenticatedUser);
        board.setTitle(boardDto.getTitle());
        board.setDescription(boardDto.getDescription());
        board.setRequest(boardDto.isRequest());
        board.setLikeCount(0);
        board.setCategory(boardDto.getCategory());
        board.setCreditPrice(boardDto.getCreditPrice());
        board.setClosed(false); // 기본값: 모집 중
        board.setCreatedAt(LocalDateTime.now());

        boardRepository.save(board);
    }

    // 📌 게시글 수정 (본인만 가능)
    @Transactional
    public void updateBoard(Long boardId, BoardDto boardDto, User authenticatedUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        board.setTitle(boardDto.getTitle());
        board.setDescription(boardDto.getDescription());
        board.setRequest(boardDto.isRequest());
        board.setCategory(boardDto.getCategory());
        board.setCreditPrice(boardDto.getCreditPrice());
        board.setUpdatedAt(LocalDateTime.now());
    }

    // 📌 게시글 삭제 (본인만 가능)
    @Transactional
    public void deleteBoard(Long boardId, User authenticatedUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("게시판 작성자가 아니므로 삭제 권한이 없습니다.");
        }
        boardRepository.delete(board);
    }

    // 📌 게시판 글 삭제 (관리자 권한)
    @Transactional
    public void deleteBoardByAdmin(Long boardId, User admin) {
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        boardRepository.delete(board);
    }

    // 📌 게시글 모집 상태 변경 (모집 완료 ↔ 모집 중)
    @Transactional
    public void toggleBoardStatus(Long boardId, User authenticatedUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("게시글 작성자만 모집 상태를 변경할 수 있습니다.");
        }

        board.setClosed(!board.isClosed());
        boardRepository.save(board);
    }

    // 📌 게시글 카테고리별 조회 (최신순 정렬)
    public List<BoardDto> getBoardsByCategory(Category category) {
        return boardRepository.findByCategory(category).stream()
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .map(board -> new BoardDto(
                        board.getId(),
                        board.getOwner().getId(),
                        board.getTitle(),
                        board.getDescription(),
                        board.isRequest(),
                        board.getLikeCount(),
                        board.getCategory(),
                        board.getCreditPrice(),
                        board.isClosed(),
                        board.getCreatedAt(),
                        board.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 📌 게시글 상세 조회
    public BoardDto getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return new BoardDto(
                board.getId(),
                board.getOwner().getId(),
                board.getTitle(),
                board.getDescription(),
                board.isRequest(),
                board.getLikeCount(),
                board.getCategory(),
                board.getCreditPrice(),
                board.isClosed(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }

    // 📌 게시글 작성자가 참여 승인 (PENDING → APPROVED, 크레딧 즉시 이전)
    @Transactional
    public void approveParticipation(Long participationId, User owner) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (participation.getStatus() != ParticipationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 참가 요청입니다.");
        }

        if (!participation.getBoard().getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("해당 게시글의 작성자만 승인할 수 있습니다.");
        }

        // 참가 승인 처리
        participation.setStatus(ParticipationStatus.APPROVED);
        participation.setApprovedAt(LocalDateTime.now());

        Credit credit = participation.getCredit();
        credit.setStatus(CreditStatus.COMPLETED);

        User sender = credit.getFromUser();
        User receiver = credit.getToUser();

        sender.setCredit(sender.getCredit() - credit.getAmount());
        receiver.setCredit(receiver.getCredit() + credit.getAmount());

        creditRepository.save(credit);
        boardParticipationRepository.save(participation);
    }
    // 📌 게시글 작성자가 참여 거절 (PENDING → REJECTED, 크레딧 환불)
    @Transactional
    public void rejectParticipation(Long participationId, User owner) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (participation.getStatus() != ParticipationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 참가 요청입니다.");
        }

        if (!participation.getBoard().getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("해당 게시글의 작성자만 거절할 수 있습니다.");
        }

        // 참가 거부 처리
        participation.setStatus(ParticipationStatus.REJECTED);

        // 📌 크레딧 환불 (SEND → REFUNDED)
        Credit credit = participation.getCredit();
        credit.setStatus(CreditStatus.REFUNDED);

        User sender = credit.getFromUser();
        sender.setCredit(sender.getCredit() + credit.getAmount());

        creditRepository.save(credit);
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
