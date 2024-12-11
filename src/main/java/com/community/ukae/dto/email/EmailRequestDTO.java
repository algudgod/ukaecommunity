package com.community.ukae.dto.email;

import lombok.Data;

@Data
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String content;
}
