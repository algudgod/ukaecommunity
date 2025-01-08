package com.community.ukae.controller.comment;

import com.community.ukae.dto.comment.CommentRequestDTO;
import com.community.ukae.dto.comment.CommentResponseDTO;
import com.community.ukae.entity.comment.Comment;
import com.community.ukae.entity.user.User;
import com.community.ukae.service.comment.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comment")
@RequiredArgsConstructor
public class CommentRestController {

    private static final Logger logger = LoggerFactory.getLogger(CommentRestController.class);

    private final CommentService commentService;

    @PostMapping("/addComment")
    public ResponseEntity<CommentResponseDTO> addComment(@RequestBody CommentRequestDTO commentRequest, HttpSession session) {

        try {
            logger.info("댓글 등록 요청: {}", commentRequest.getContent());

            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            CommentResponseDTO savedComment = commentService.addComment(commentRequest, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } catch (Exception e) {
            logger.error("댓글 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    // 댓글 목록 조회
    @GetMapping("/listComment/{boardNo}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByBoardNo(@PathVariable int boardNo) {
        logger.info("댓글 목록 조회 요청: boardNo={}", boardNo);

        try {
            // 댓글 목록 조회
            List<CommentResponseDTO> commentResponseList = commentService.getCommentListByBoardNo(boardNo);
            logger.info("댓글 목록 조회 성공: boardNo={}, 댓글 수={}", boardNo, commentResponseList.size());

            return ResponseEntity.ok(commentResponseList);
        } catch (Exception e) {
            logger.error("댓글 목록 조회 중 오류 발생: boardNo={}", boardNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 댓글 수정
    @PutMapping("/updateComment/{commentNo}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable int commentNo,
                                                            @RequestBody CommentRequestDTO commentRequest, HttpSession session) {
        logger.info("댓글 수정 요청: commentNo={}, content={}", commentNo, commentRequest.getContent());

        try {
            // 사용자 인증 확인
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            CommentResponseDTO updatedComment = commentService.updateComment(commentNo, commentRequest, user);
            return ResponseEntity.ok(updatedComment);

        } catch (Exception e) {
            logger.error("댓글 수정 중 오류 발생: commentNo={}", commentNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 댓글 삭제
    @DeleteMapping("deleteComment/{commentNo}")
    public ResponseEntity<Void> deleteComment(@PathVariable int commentNo, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            commentService.deleteComment(commentNo, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("댓글 삭제 중 오류 발생: commentNo={}", commentNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
