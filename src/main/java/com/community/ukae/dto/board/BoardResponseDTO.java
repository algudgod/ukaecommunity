package com.community.ukae.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDTO {

    private int boardNo;
    private String mainCategory;
    private String subCategory;
    private String title;
    private String nickname;
    private String content;
    private int viewCount;
    private LocalDateTime createDate;
    private int categoryBoardNo;

    private List<String> imageUrls; // 이미지 URL 리스트


}
