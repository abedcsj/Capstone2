package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.dto.BoardDto;
import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardParticipationRepository boardParticipationRepository;

    @Transactional
    public void createBoard(Long userId, BoardDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Board board = new Board();
        board.setOwner(user);
        board.setTitle(dto.getTitle());
        board.setDescription(dto.getDescription());
        board.setCreditPrice(dto.getCreditPrice());
        board.setCategory(dto.getCategory());
        board.setClosed(false);

        boardRepository.save(board);
    }

    @Transactional
    public void updateBoard(Long userId, Long boardId, BoardDto dto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        if (!board.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("수정 권한 없음");
        }
        board.setTitle(dto.getTitle());
        board.setDescription(dto.getDescription());
        board.setCreditPrice(dto.getCreditPrice());
        board.setCategory(dto.getCategory());
        board.setClosed(dto.isClosed());
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!board.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("삭제 권한 없음");
        }

        boardRepository.delete(board);
    }

    public List<BoardDto> searchBoardsByTitle(String keyword) {
        List<Board> boards = boardRepository.findByTitleContainingIgnoreCase(keyword);
        return boards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void likeBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        board.increaseLikeCount(); // 좋아요 1 증가
    }


    public List<BoardDto> getBoardsPaged(int page) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createdAt"));
        return boardRepository.findAll(pageable)
                .map(this::toDto)
                .getContent();
    }

    @Transactional
    public void toggleClosed(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!board.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("상태 변경 권한 없음");
        }

        board.setClosed(!board.isClosed());
    }

    private BoardDto toDto(Board board) {
        return new BoardDto(
                board.getId(),
                board.getOwner().getId(),
                board.getTitle(),
                board.getDescription(),
                board.getLikeCount(),
                board.getCategory(),
                board.getCreditPrice(),
                board.isClosed(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }

    @Transactional
    public void approveParticipation(Long participationId, User owner) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보 없음"));

        Board board = participation.getBoard();
        if (!board.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("게시글 작성자만 승인할 수 있습니다.");
        }

        participation.setStatus(ParticipationStatus.APPROVED);
        board.getParticipants().add(participation);

        int creditAmount = participation.getCreditAmount();
        User receiver = board.getOwner();
        receiver.setCredit(receiver.getCredit() + creditAmount);
        userRepository.save(receiver);

        boardParticipationRepository.save(participation);
    }

    @Transactional
    public void rejectParticipation(Long participationId, User owner) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보 없음"));

        Board board = participation.getBoard();
        if (!board.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("게시글 작성자만 거절할 수 있습니다.");
        }

        participation.setStatus(ParticipationStatus.REJECTED);

        int creditAmount = participation.getCreditAmount();
        User sender = participation.getUser();
        sender.setCredit(sender.getCredit() + creditAmount);
        userRepository.save(sender);

        boardParticipationRepository.save(participation);
    }

    public List<BoardParticipationDto> getApprovedParticipations(Long boardId) {
        List<BoardParticipation> approvedParticipations =
                boardParticipationRepository.findByBoardIdAndStatus(boardId, ParticipationStatus.APPROVED);

        return approvedParticipations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    private BoardParticipationDto toDto(BoardParticipation participation) {
        return new BoardParticipationDto(
                participation.getId(),
                participation.getBoard().getId(),
                participation.getUser().getId(),
                participation.getStatus(),
                participation.getRequestedAt(),
                participation.getApprovedAt(),
                participation.getCreditAmount()
        );

    }

    public BoardDto getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        return toDto(board);
    }
}
