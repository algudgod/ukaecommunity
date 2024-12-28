package com.community.ukae.service.kakao;

import com.community.ukae.dto.kakao.KakaoRequestDTO;
import com.community.ukae.entity.user.User;
import com.community.ukae.service.s3.S3Service;
import com.community.ukae.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
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
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Object kakaoAccountObj = attributes.get("kakao_account");

        if (!(kakaoAccountObj instanceof Map)) {
            throw new RuntimeException("kakao_account 정보가 Map 형태가 아닙니다.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;

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
            return "0" + phone.substring(3).replaceAll("\\s|-", "");
        }
        return phone.replaceAll("\\s|-", "");
    }

    // 회원가입 뷰 생성
    public ModelAndView createAddUserForKakao(KakaoRequestDTO kakaoRequest) {
        boolean isRegistered = isUserAlreadyRegistered(kakaoRequest.getName(), kakaoRequest.getEmail());
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

    // 로그인 처리
    public String LoginForKaKao(KakaoRequestDTO kakaoUserInfo, HttpSession session, Model model, S3Service s3Service) {
        boolean isRegistered = isUserAlreadyRegistered(kakaoUserInfo.getName(), kakaoUserInfo.getEmail());
        if (isRegistered) {
            logger.info("기존 사용자 로그인 성공!");
            User user = findByNameAndEmail(kakaoUserInfo.getName(), kakaoUserInfo.getEmail());
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            logger.warn("미가입 사용자입니다.");
            String kakaoLoginImageUrl = s3Service.getFileUrl("kakao_login_medium_narrow.png");
            model.addAttribute("kakaoLoginImageUrl", kakaoLoginImageUrl);
            model.addAttribute("error", "가입된 정보가 없습니다.");
            return "user/login";
        }
    }
}