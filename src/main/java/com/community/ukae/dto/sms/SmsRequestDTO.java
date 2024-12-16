package com.community.ukae.dto.sms;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsRequestDTO {
    @NotBlank(message = "발신번호는 필수입니다.")
    private String from;

    @NotBlank(message = "수신번호는 필수입니다.")
    private String to;

    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String text;
}
