package com.community.ukae.service.sms;

import com.community.ukae.dto.sms.SmsRequestDTO;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class SmsService {

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

    // 일반 단일 메세지 발송
    public void sendSms(SmsRequestDTO smsRequest) {
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(smsRequest.getTo());
        message.setText(smsRequest.getText());

        messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    // 인증번호 생성
    public String createAuthCode(){
        Random random = new Random();
        StringBuilder authCode = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            authCode.append(random.nextInt(10)); // 0~9 사이 숫자 추가
        }
        return authCode.toString();
    }

    // 인증번호를 포함한 단일 메세지 발송
    public void sendSmsWithAuthCode(SmsRequestDTO smsRequest){
        String authCode = createAuthCode();
        long expirationTime = System.currentTimeMillis() + AUTH_CODE_EXPIRATION_TIME;
        codeStorage.put(smsRequest.getTo(), authCode);
        expiryStorage.put(smsRequest.getTo(), expirationTime);

        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(smsRequest.getTo());
        message.setText("[유쾌 커뮤니티] 본인확인 인증번호 [" + authCode + "]입니다. \"타인노출 금지\"");

        messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    // 인증번호 검증
    public boolean verifyAuthCode(String to, String authCode){
        String storageCode = codeStorage.get(to);
        Long expiryTime = expiryStorage.get(to);

        if (storageCode == null || expiryTime == null || System.currentTimeMillis() > expiryTime) {
            codeStorage.remove(to);
            expiryStorage.remove(to);
            return false; // 인증 실패: 코드 없음, 만료됨
        }
        return storageCode.equals(authCode); // 인증 성공 여부 반환
    }

}
