package com.community.ukae.repository.imageFile;

import com.community.ukae.entity.imageFile.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Integer> {

    List<ImageFile> findByBoard_BoardNo(int boardNo); // 게시글 번호로 이미지 조회
    List<ImageFile> findByComment_CommentNoIn(List<Integer> commentNos);
}
