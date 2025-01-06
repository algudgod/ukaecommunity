package com.community.ukae.service.email;

import com.community.ukae.dto.email.EmailRequestDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final Map<String, Long> tokenStorage = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRATION_TIME = 3 * 60 * 1000; // 3분


    // 이메일 발송 공통 로직
    private void sendMimeMessage(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);
            mimeMessageHelper.setFrom(System.getenv("EMAIL_SMTP_USERNAME"));

            mailSender.send(mimeMessage);
            logger.info("이메일 발송 성공: {}", to);
        } catch (Exception e) {
            logger.error("이메일 발송 실패: to={}, subject={}, 이유={}", to, subject, e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다: " + to, e);
        }
    }

     //일반 이메일 발송
    public void sendEmail(EmailRequestDTO emailRequest) {
        sendMimeMessage(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getContent());
    }

    // 토큰 생성
    public String createToken(String email) {
        String token = UUID.randomUUID().toString();  // 고유한 토큰 생성
        long expirationTime = System.currentTimeMillis() + TOKEN_EXPIRATION_TIME;  // 만료 시간 계산
        tokenStorage.put(token, expirationTime);  // 토큰과 만료 시간을 매핑하여 저장
        logger.info("토큰 생성 완료 {}", email);
        return token;
    }

    // 토큰 검증
    public void validateToken(String token) {
        Long expirationTime = tokenStorage.remove(token); // 삭제와 동시에 만료 시간 가져오기
        if (expirationTime == null) {
            logger.warn("토큰 검증 실패 - 유효하지 않은 토큰: {}", token);
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        if (System.currentTimeMillis() > expirationTime) {
            logger.warn("토큰 검증 실패 - 토큰 만료: {}", token);
            throw new RuntimeException("토큰이 만료되었습니다.");
        }
        logger.info("토큰 검증 성공: {}", token);
    }

    // 이메일 인증 발송
    public void sendEmailWithToken(String to) {
        String token = createToken(to);
        String link = "http://localhost:8080/email/verifyEmail?token=" + token;
        String content = "<p>아래 링크를 클릭하여 인증을 완료해주세요</p>" +
                "<p>유쾌 커뮤니티 이메일 인증하기:</p>" +
                "<a href=\"" + link + "\">인증</a>";

        sendMimeMessage(to, "유쾌 커뮤니티 회원 가입 인증 이메일입니다.", content);
    }


}
