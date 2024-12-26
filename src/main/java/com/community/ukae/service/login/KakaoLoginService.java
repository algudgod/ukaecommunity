package com.community.ukae.service.login;

import com.community.ukae.config.KakaoConfig;
import com.community.ukae.dto.kakao.KakaoRequestDTO;
import com.community.ukae.service.user.UserService;

import com.community.ukae.util.HttpRequestUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final Logger logger = LoggerFactory.getLogger(KakaoLoginService.class);
    private final KakaoConfig kakaoConfig;
    private final UserService userService;

    // 인증 URL 생성
    public String getKakaoAuthUrl() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + kakaoConfig.getRestApiKey()
                + "&redirect_uri=" + kakaoConfig.getRedirectUri()
                + "&prompt=login"; // 항상 로그인 화면을 표시

        logger.info("KakaoAuthUrl: {}", kakaoAuthUrl);
        return kakaoAuthUrl;
    }

    // 카카오 액세스 토큰 요청
    public String getKakaoAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        // 요청 파라미터 구성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoConfig.getRestApiKey());
        params.add("redirect_uri", kakaoConfig.getRedirectUri());
        params.add("code", code);

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // HttpRequestUtil 사용
        Map<String, Object> responseBody = HttpRequestUtil.sendHttpRequest(
                tokenUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        // 응답 처리
        return (String) responseBody.get("access_token");
    }

    // 사용자 정보 요청
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        // 요청 엔티티 생성
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // HttpRequestUtil 사용하여 요청 보내기
        return HttpRequestUtil.sendHttpRequest(
                userInfoUrl,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
    }

    public boolean isUserAlreadyRegistered(String name, String email) {
        return userService.findByNameAndEmail(name, email).isPresent();
    }

    // 사용자 정보 DTO로 매핑
    public KakaoRequestDTO mapToKakaoRequestDTO(Map<String, Object> userInfo) {
        // 사용자 정보 매핑
        Object kakaoAccountObj = userInfo.get("kakao_account");
        if (!(kakaoAccountObj instanceof Map)) {
            throw new RuntimeException("kakao_account 정보가 Map 형태가 아닙니다.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;

        String rawPhone = (String) kakaoAccount.get("phone_number");
        String formattedPhone = formatPhoneNumber(rawPhone);

        // DTO 반환
        return new KakaoRequestDTO(
                (String) kakaoAccount.get("name"),
                (String) kakaoAccount.get("email"),
                formattedPhone
        );
    }

    // 핸드폰 번호 포맷팅 메서드
    private String formatPhoneNumber(String phone) {
        if (phone.startsWith("+82")) {
            // "+82"를 "0"으로 변경
            return "0" + phone.substring(3).replaceAll("\\s|-", "");
        }
        // 다른 국가 코드가 붙은 경우 그대로 반환
        return phone.replaceAll("\\s|-", "");
    }




}