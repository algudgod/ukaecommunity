package com.community.ukae.controller.comment;

import com.community.ukae.dto.comment.CommentRequestDTO;
import com.community.ukae.dto.comment.CommentResponseDTO;
import com.community.ukae.entity.comment.Comment;
import com.community.ukae.entity.user.User;
import com.community.ukae.service.comment.CommentService;
import com.community.ukae.service.s3.S3Service;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentRestController {

    private final CommentService commentService;
    private final S3Service s3Service;

    @PostMapping("/addComment")
    public ResponseEntity<CommentResponseDTO> addComment(@RequestParam(value = "file", required = false) MultipartFile file,
                                                         @RequestParam("content") String content,
                                                         @RequestParam("boardNo") int boardNo,
                                                         HttpSession session) {
        try {
            log.info("댓글 등록 요청: content={}, boardNo={}", content, boardNo);

            // 세션에서 사용자 정보 가져오기
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 댓글 요청 DTO 생성
            CommentRequestDTO commentRequest = new CommentRequestDTO();
            commentRequest.setContent(content);
            commentRequest.setBoardNo(boardNo);

            // 댓글 생성 및 저장
            CommentResponseDTO savedComment = commentService.addComment(commentRequest, user, file);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);

        } catch (Exception e) {
            log.error("댓글 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 댓글 목록 조회
    @GetMapping("/listComment/{boardNo}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByBoardNo(@PathVariable int boardNo) {
        log.info("댓글 목록 조회 요청: boardNo={}", boardNo);

        try {
            // 댓글 목록 조회
            List<CommentResponseDTO> commentResponseList = commentService.getCommentListByBoardNo(boardNo);
            log.info("댓글 목록 조회 성공: boardNo={}, 댓글 수={}", boardNo, commentResponseList.size());

            return ResponseEntity.ok(commentResponseList);
        } catch (Exception e) {
            log.error("댓글 목록 조회 중 오류 발생: boardNo={}", boardNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 댓글 수정
    @PutMapping("/updateComment/{commentNo}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable int commentNo,
                                                            @RequestBody CommentRequestDTO commentRequest, HttpSession session) {
        log.info("댓글 수정 요청: commentNo={}, content={}", commentNo, commentRequest.getContent());

        try {
            // 사용자 인증 확인
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            CommentResponseDTO updatedComment = commentService.updateComment(commentNo, commentRequest, user);
            return ResponseEntity.ok(updatedComment);

        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생: commentNo={}", commentNo, e);
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
            log.error("댓글 삭제 중 오류 발생: commentNo={}", commentNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
