package com.community.ukae.service.comment;

import com.community.ukae.dto.comment.CommentRequestDTO;
import com.community.ukae.dto.comment.CommentResponseDTO;
import com.community.ukae.entity.board.Board;
import com.community.ukae.entity.comment.Comment;
import com.community.ukae.entity.imageFile.ImageFile;
import com.community.ukae.entity.user.User;
import com.community.ukae.repository.board.BoardRepository;
import com.community.ukae.repository.comment.CommentRepository;
import com.community.ukae.repository.imageFile.ImageFileRepository;
import com.community.ukae.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final ImageFileRepository imageFileRepository;
    private final S3Service s3Service;

    // 게시글 조회
    public Board findBoardEntityByBoardNo(int boardNo) {
        return boardRepository.findByBoardNo(boardNo)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));
    }

    // 댓글 생성
    public CommentResponseDTO addComment(CommentRequestDTO commentRequest, User user, MultipartFile imageFile) throws IOException {
        logger.info("댓글 생성 요청: boardNo={}, content={}, user={}",
                commentRequest.getBoardNo(), commentRequest.getContent(), user.getNickname());

        // 게시글 조회
        Board board = findBoardEntityByBoardNo(commentRequest.getBoardNo());

        // 댓글 엔티티 생성 및 설정
        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .user(user)
                .board(board)
                .createDate(LocalDateTime.now())
                .build();

        // 댓글 저장
        Comment savedComment = commentRepository.save(comment);
        logger.info("댓글 저장 완료: commentNo={}", savedComment.getCommentNo());

        // 이미지 업로드 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            uploadCommentImage(imageFile, savedComment);
            logger.info("댓글 이미지 업로드 완료: commentNo={}", savedComment.getCommentNo());
        }

        return CommentResponseDTO.builder()
                .commentNo(savedComment.getCommentNo())
                .content(savedComment.getContent())
                .nickname(savedComment.getUser().getNickname())
                .createDate(savedComment.getCreateDate())
                .build();
    }

    // 댓글 이미지 단일 업로드
    public void uploadCommentImage(MultipartFile file, Comment comment) throws IOException {

        String fileUrl = s3Service.uploadFile(file); // 공통 S3 업로드 호출

        // 이미지 엔티티 생성 및 설정
        ImageFile imageFile = ImageFile.builder()
                .comment(comment) // 댓글과 연결
                .board(comment.getBoard()) // 댓글에 연결된 게시글과 연결
                .imageUrl(fileUrl) // 반환 받은 S3 Url 저장
                .build();

        imageFileRepository.save(imageFile); // DB에 저장
        logger.info("댓글 이미지 업로드 성공: commentNo={}, imageUrl={}", comment.getCommentNo(), fileUrl);
    }

    // 댓글 목록
    public List<CommentResponseDTO> getCommentListByBoardNo(int boardNo) {
        logger.info("댓글 목록 요청: boardNo={}", boardNo);

        List<Comment> comments = commentRepository.findByBoard_boardNo(boardNo);
        logger.info("조회된 댓글 수: {}", comments.size());

        List<CommentResponseDTO> commentResponseList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponseDTO responseDTO = CommentResponseDTO.builder()
                    .commentNo(comment.getCommentNo())
                    .nickname(comment.getUser().getNickname())
                    .content(comment.getContent())
                    .createDate(comment.getCreateDate())
                    .build();

            commentResponseList.add(responseDTO);
        }
        logger.info("댓글 목록 요청 성공: boardNo={}", boardNo);
        return commentResponseList;
    }

    // 댓글 수정
    public CommentResponseDTO updateComment(int commentNo, CommentRequestDTO commentRequest, User user) {
        logger.info("댓글 수정 요청: commentNo={}, content={}", commentNo, commentRequest.getContent());

        // 댓글 조회
        Comment comment = findCommentByCommentNo(commentNo);

        // 수정 권한 확인
        if (!comment.getUser().getLoginId().equals(user.getLoginId())) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        comment.setContent(commentRequest.getContent());
        comment.setUpdateDate(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        logger.info("댓글 수정 완료: commentNo={}", commentNo);

        // DTO 변환 후 반환
        return CommentResponseDTO.builder()
                .commentNo(updatedComment.getCommentNo())
                .content(updatedComment.getContent())
                .nickname(updatedComment.getUser().getNickname())
                .createDate(updatedComment.getCreateDate())
                .build();
    }

    // 댓글 삭제
    public void deleteComment(int commentNo, User user) {
        logger.info("댓글 삭제 요청: commentNo={}, user={}", commentNo, user.getNickname());

        // 댓글 조회
        Comment comment = findCommentByCommentNo(commentNo);

        // 댓글 작성자 검증
        if (!comment.getUser().getLoginId().equals(user.getLoginId())) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.deleteById(commentNo);
        logger.info("댓글 삭제 완료: commentNo={}", commentNo);
    }

    // 댓글 조회
    public Comment findCommentByCommentNo(int commentNo){
        return commentRepository.findByCommentNo(commentNo)
                .orElseThrow(() -> new NoSuchElementException("해당 댓글을 찾을 수 없습니다."));
    }

}