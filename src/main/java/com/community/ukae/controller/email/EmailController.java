package com.community.ukae.controller.email;

import com.community.ukae.dto.email.EmailRequestDTO;
import com.community.ukae.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        try {
            // 인증 성공 시
            emailService.validateToken(token);
            model.addAttribute("message", "이메일 인증이 완료되었습니다!");
            model.addAttribute("messageColor", "green");
        } catch (RuntimeException e) {
            // 인증 실패 시
            model.addAttribute("message", "인증 실패: " + e.getMessage());
            model.addAttribute("messageColor", "red");
        }
        return "verification/emailVerification";
    }
}
