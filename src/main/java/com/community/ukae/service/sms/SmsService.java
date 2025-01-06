package com.community.ukae.service.sms;

import com.community.ukae.dto.sms.SmsRequestDTO;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    private final DefaultMessageService messageService;
    private final Map<String, String> codeStorage = new HashMap<>(); // 전화번호와 인증 코드 저장
    private final Map<String, Long> expiryStorage = new HashMap<>(); // 코드 만료 시간 저장
    private static final long AUTH_CODE_EXPIRATION_TIME = 5 * 60 * 1000; // 5분
    private final String fromNumber;

    public SmsService(@Value("${coolsms.api.key}") String apiKey,
                      @Value("${coolsms.api.secret}") String apiSecret,
                      @Value("${coolsms.from.number}") String fromNumber) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        this.fromNumber = fromNumber; // 환경 변수에서 가져온 발신 번호
    }

    public long getAuthCodeExpirationTime() {
        return AUTH_CODE_EXPIRATION_TIME;
    }

    // 단일 SMS 발송
    public void sendSms(SmsRequestDTO smsRequest) {
        logger.info("SMS 발송 요청: to={}, text={}", smsRequest.getTo(), smsRequest.getText());

        try {
            Message message = new Message();
            message.setFrom(fromNumber);
            message.setTo(smsRequest.getTo());
            message.setText(smsRequest.getText());

            messageService.sendOne(new SingleMessageSendingRequest(message));
            logger.info("SMS 발송 성공: to={}", smsRequest.getTo());
        } catch (Exception e) {
            logger.error("SMS 발송 실패: to={}, 이유={}", smsRequest.getTo(), e.getMessage(), e);
            throw new RuntimeException("SMS 발송 실패: " + e.getMessage(), e);
        }
    }

    // 인증번호 생성
    public String createAuthCode(){
        StringBuilder authCode = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            authCode.append(random.nextInt(10)); // 0~9 사이 숫자 추가
        }
        logger.debug("인증번호 생성 완료: authCode={}", authCode);
        return authCode.toString();
    }

    // 인증번호를 포함한 단일 메세지 발송
    public void sendSmsWithAuthCode(SmsRequestDTO smsRequest){
        String authCode = createAuthCode();
        long expirationTime = System.currentTimeMillis() + AUTH_CODE_EXPIRATION_TIME;
        codeStorage.put(smsRequest.getTo(), authCode);
        expiryStorage.put(smsRequest.getTo(), expirationTime);

        logger.info("인증번호 발송 준비: to={}, authCode={}, expiryTime={}", smsRequest.getTo(), authCode, expirationTime);

        try {
            Message message = new Message();
            message.setFrom(fromNumber);
            message.setTo(smsRequest.getTo());
            message.setText("[유쾌 커뮤니티] 본인확인 인증번호 [" + authCode + "]입니다. \"타인노출 금지\"");

            messageService.sendOne(new SingleMessageSendingRequest(message));
            logger.info("인증번호 SMS 발송 성공: to={}", smsRequest.getTo());
        } catch (Exception e) {
            logger.error("인증번호 SMS 발송 실패: to={}, 이유={}", smsRequest.getTo(), e.getMessage(), e);
            throw new RuntimeException("인증번호 SMS 발송 실패: " + e.getMessage(), e);
        }
    }

    // 인증번호 검증
    public boolean verifyAuthCode(String to, String authCode){
        Long expiryTime = expiryStorage.get(to);
        String storageCode = codeStorage.get(to);

        if (expiryTime == null || storageCode == null) {
            // 코드 없음 또는 만료 시간 없음
            logger.warn("인증 실패 - 코드 또는 만료 시간 없음: to={}", to);
            codeStorage.remove(to);
            expiryStorage.remove(to);
            return false;
        }
        if (System.currentTimeMillis() > expiryTime) {
            // 인증번호 만료
            logger.warn("인증 실패 - 인증번호 만료: to={}, 저장된 만료 시간={}", to, expiryTime);
            codeStorage.remove(to);
            expiryStorage.remove(to);
            return false;
        }
        logger.info("인증 성공: to={}, 입력된 인증번호={}", to, authCode);
        return storageCode.equals(authCode); // 인증 성공 여부 반환
    }


}
