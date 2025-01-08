package com.community.ukae.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    private int commentNo;
    private int boardNo;
    private String nickname;
    @NotBlank(message = "댓글은 필수 항목입니다.")
    @Size(min = 1, max = 300, message = "댓글은 1자 이상, 300자 이하로 입력해주세요.")
    private String content;

    private LocalDateTime createDate;

}
