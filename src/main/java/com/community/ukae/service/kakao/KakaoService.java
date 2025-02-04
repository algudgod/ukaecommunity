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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final Logger logger = LoggerFactory.getLogger(KakaoService.class);
    private final UserService userService;

    // 카카오 사용자 정보 가져오기
    public KakaoRequestDTO getKakaoUserInfo(OAuth2AuthenticationToken authentication) {
        try {
            OAuth2User oAuth2User = authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Object kakaoAccountObj = attributes.get("kakao_account");

            if (!(kakaoAccountObj instanceof Map)) {
                throw new RuntimeException("kakao_account 정보가 Map 형태가 아닙니다.");
            }

            Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;

            String name = (String) kakaoAccount.get("name");
            String email = (String) kakaoAccount.get("email");
            String phone = (String) kakaoAccount.get("phone_number");
            logger.info("카카오 사용자 정보: name={}, email={}", name, email);

            // formatPhoneNumber 메서드의 반환값인 Optional을 처리
            Optional<String> formattedPhoneOpt = formatPhoneNumber(phone);
            String formattedPhone = formattedPhoneOpt.orElse(null); // 값이 없을 경우 null로 설정

            return new KakaoRequestDTO(name, email, formattedPhone);
        } catch (Exception e) {
            logger.error("카카오 사용자 정보 가져오기 실패: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 사용자 정보를 가져오는 데 실패했습니다.");
        }
    }

    // 사용자 조회
    public User findByNameAndEmail(String name, String email) {
        return userService.findByNameAndEmail(name, email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 핸드폰 번호 포맷팅
    private Optional<String> formatPhoneNumber(String phone) {
        if (phone == null) {
            return Optional.empty(); // 전화번호가 없는 경우 빈 Optional 반환
        }
        // 한국 국제번호 +82를 국내 번호 형식으로 변환
        if (phone.startsWith("+82")) {
            phone = "0" + phone.substring(3);
        }
        // 공백 및 하이픈 제거
        String formattedPhone = phone.replaceAll("[\\s-]", "");
        return Optional.of(formattedPhone);
    }

    // 회원가입 뷰 생성
    public ModelAndView createAddUserForKakao(KakaoRequestDTO kakaoRequest) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            // 사용자 이름과 이메일을 기준으로 기존 회원 여부를 확인
            userService.findByNameAndEmail(kakaoRequest.getName(), kakaoRequest.getEmail())
                    .ifPresentOrElse(
                            user -> {
                                // 사용자가 이미 가입된 경우
                                modelAndView.setViewName("user/alreadyAddUser");
                                modelAndView.addObject("message", "이미 가입된 사용자입니다.");
                            },
                            () -> {
                                // 사용자가 가입되지 않은 경우
                                modelAndView.setViewName("user/addKakaoUserForm");
                                modelAndView.addObject("name", kakaoRequest.getName());
                                modelAndView.addObject("email", kakaoRequest.getEmail());
                                modelAndView.addObject("phone", kakaoRequest.getPhone());
                            });
        } catch (Exception e) {
            logger.error("회원가입 뷰 생성 중 오류: {}", e.getMessage(), e);
            throw new RuntimeException("회원가입 뷰 생성 중 문제가 발생했습니다.");
        }
        return modelAndView;
    }

    // 로그인 처리
    public String LoginForKaKao(KakaoRequestDTO kakaoUserInfo, HttpSession session, Model model, S3Service s3Service) {
        try {
            User user = userService.findByNameAndEmail(kakaoUserInfo.getName(), kakaoUserInfo.getEmail())
                    .orElseThrow(() -> {
                        logger.info("미가입 사용자: email={}", kakaoUserInfo.getEmail());
                        return new IllegalArgumentException("가입된 정보가 없습니다.");
                    });
            logger.info("기존 사용자 로그인 성공: email={}", kakaoUserInfo.getEmail());
            session.setAttribute("user", user);
            return "redirect:/";
        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());
            return "user/login";
        }
    }
}