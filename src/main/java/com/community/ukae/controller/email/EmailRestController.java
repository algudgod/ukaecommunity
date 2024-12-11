package com.community.ukae.controller.email;

import com.community.ukae.dto.email.EmailRequestDTO;
import com.community.ukae.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailRestController {

    private final EmailService emailService;

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequest) {

        try {
            emailService.sendEmail(emailRequest);
            return ResponseEntity.ok("이메일이 성공적으로 발송되었습니다.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이메일 발송 실패: " + e.getMessage());
        }
    }
}

