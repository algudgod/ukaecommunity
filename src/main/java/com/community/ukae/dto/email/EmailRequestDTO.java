package com.community.ukae.dto.email;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String content;

    public EmailRequestDTO(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }
}
