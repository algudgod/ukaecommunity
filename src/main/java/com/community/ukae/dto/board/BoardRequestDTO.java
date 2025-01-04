package com.community.ukae.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class BoardRequestDTO {

    private int boardNo;

    private String mainCategory;
    private String subCategory;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(min=3, max = 100, message = "3자 이상, 100자이하로 입력해주세요.")
    private String title;
    private String nickname;
    @NotBlank(message = "내용은 필수 항목입니다.")
    @Size(min = 5, max = 2000, message = "내용은 5자 이상, 2000자 이하로 입력해주세요.")
    private String content;

    private int viewCount;
    private LocalDateTime createDate;

    private int categoryBoardNo;

    private List<MultipartFile> images;

    private String tag;

}
