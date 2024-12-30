package com.community.ukae.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BoardRequestDTO {

    private int boardNo;

    private String mainCategory;
    private String subCategory;

    private String title;
    private String nickname;
    private String content;

    private int viewCount;
    private LocalDateTime createDate;

}
