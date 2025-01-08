package com.community.ukae.repository.comment;

import com.community.ukae.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 조회
    List<Comment> findByBoard_boardNo(int boardNo);
    Optional<Comment> findByCommentNo(int commentNo);

}
