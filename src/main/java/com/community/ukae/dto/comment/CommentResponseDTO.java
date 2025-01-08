package com.community.ukae.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDTO {

    private int commentNo;
    private int boardNo;
    private String nickname;
    private String content;
    private LocalDateTime createDate;

}
