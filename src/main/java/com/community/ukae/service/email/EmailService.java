package com.community.ukae.service.email;

import com.community.ukae.dto.email.EmailRequestDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;


    public void sendEmail(EmailRequestDTO emailRequest) {

        try {
        // 1.JavaMailSender에서 제공하는 MimeMessage 객체 생성
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 2. 메시지 설정
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
        mimeMessageHelper.setTo(emailRequest.getTo()); // 수신자
        mimeMessageHelper.setSubject(emailRequest.getSubject()); // 제목
        mimeMessageHelper.setText(emailRequest.getContent(), true); // 본문
        mimeMessageHelper.setFrom(System.getenv("EMAIL_SMTP_USERNAME")); // 발신자


            // 3. 이메일 발송
        mailSender.send(mimeMessage);
            logger.info("이메일 발송 성공: {}", emailRequest.getTo());

        } catch (Exception e){
                logger.error("이메일 발송 실패: {}", e.getMessage(), e);
                throw new RuntimeException("이메일 발송에 실패했습니다." + emailRequest.getTo(), e);
        }
    }


}
