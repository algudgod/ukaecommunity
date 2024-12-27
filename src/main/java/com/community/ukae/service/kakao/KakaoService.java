package com.community.ukae.service.kakao;

import com.community.ukae.dto.kakao.KakaoRequestDTO;
import com.community.ukae.entity.user.User;
import com.community.ukae.service.user.UserService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final Logger logger = LoggerFactory.getLogger(KakaoService.class);
    private final UserService userService;


    // 카카오 사용자 정보 가져오기
    public KakaoRequestDTO getKakaoUserInfo(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();

        // 전체 사용자 정보 로깅
        //logger.info("카카오 사용자 전체 정보: {}", oAuth2User.getAttributes());

        // kakao_account 객체 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Object kakaoAccountObj = attributes.get("kakao_account");

        if (!(kakaoAccountObj instanceof Map)) {
            throw new RuntimeException("kakao_account 정보가 Map 형태가 아닙니다.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;

        // 사용자 정보를 DTO로 매핑
        String name = (String) kakaoAccount.get("name");
        String email = (String) kakaoAccount.get("email");
        String phone = (String) kakaoAccount.get("phone_number");

        return new KakaoRequestDTO(name, email, formatPhoneNumber(phone));
    }

    // 사용자 가입 여부 확인
    public boolean isUserAlreadyRegistered(String name, String email) {
        return userService.findByNameAndEmail(name, email).isPresent();
    }

    // 사용자 조회
    public User findByNameAndEmail(String name, String email) {
        return userService.findByNameAndEmail(name, email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 핸드폰 번호 포맷팅
    private String formatPhoneNumber(String phone) {
        if (phone == null) return null;
        if (phone.startsWith("+82")) {
            // "+82"를 "0"으로 변경
            return "0" + phone.substring(3).replaceAll("\\s|-", "");
        }
        // 다른 국가 코드가 붙은 경우 그대로 반환
        return phone.replaceAll("\\s|-", "");
    }

    // 가입 여부에 따른 반환
    public ModelAndView createModelAndView(boolean isRegistered, KakaoRequestDTO kakaoRequest) {
        String viewName = isRegistered ? "user/alreadyAddUser" : "user/addKakaoUserForm";
        ModelAndView modelAndView = new ModelAndView(viewName);

        if (isRegistered) {
            modelAndView.addObject("message", "이미 가입된 사용자입니다.");
        } else {
            modelAndView.addObject("name", kakaoRequest.getName());
            modelAndView.addObject("email", kakaoRequest.getEmail());
            modelAndView.addObject("phone", kakaoRequest.getPhone());
        }

        return modelAndView;
    }


}