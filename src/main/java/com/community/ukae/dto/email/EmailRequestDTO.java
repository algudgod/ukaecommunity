package com.community.ukae.dto.email;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequestDTO {
    @NotBlank(message = "수신자는 필수입니다.")
    private String to;
    @NotBlank(message = "제목은 필수입니다.")
    private String subject;
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    public EmailRequestDTO(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }
}
