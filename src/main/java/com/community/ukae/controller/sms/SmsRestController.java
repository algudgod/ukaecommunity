package com.community.ukae.controller.sms;

import com.community.ukae.dto.sms.SmsRequestDTO;
import com.community.ukae.service.sms.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/sms")
@RequiredArgsConstructor
public class SmsRestController {

    private final SmsService smsService;

    @PostMapping("sendSms")
    public ResponseEntity<String> sendSms(@Valid @RequestBody SmsRequestDTO smsRequest){
        smsService.sendSms(smsRequest);
        return ResponseEntity.ok("문자 전송 성공!");
    }

    // 인증번호를 포함한 단일 메세지 발송
    @PostMapping("sendSmsWithAuthCode")
    public ResponseEntity<String> sendSmsWithAuthCode(@RequestBody SmsRequestDTO smsRequest) {
        smsService.sendSmsWithAuthCode(smsRequest);
        return ResponseEntity.ok("인증 코드가 전송되었습니다.");
    }

    // 인증번호 검증
    @PostMapping("verifyAuthCode")
    public ResponseEntity<String> verifyAuthCode(@RequestParam String to, @RequestParam String authCode) {
        boolean result = smsService.verifyAuthCode(to, authCode);
        if (result) {
            return ResponseEntity.ok("인증에 성공했습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증에 실패했습니다. 코드가 일치하지 않거나 만료되었습니다.");
        }
    }
}
