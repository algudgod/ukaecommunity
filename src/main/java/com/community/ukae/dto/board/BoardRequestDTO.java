package com.community.ukae.dto.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardRequestDTO {

    private int boardNo;

    private String category;

    private String title;
    private String nickname;
    private String content;

    private int viewCount;
    private LocalDateTime createDate;

}
