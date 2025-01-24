package com.community.ukae.controller.kakao;

import com.community.ukae.dto.kakao.KakaoRequestDTO;
import com.community.ukae.service.kakao.KakaoService;
import com.community.ukae.service.s3.S3Service;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final S3Service s3Service;
    private final KakaoService kakaoService;
    private final Logger logger = LoggerFactory.getLogger(KakaoController.class);

    // 카카오 회원가입 처리
    @GetMapping("addUser")
    public ModelAndView addKakaoUser(OAuth2AuthenticationToken authentication) {
        KakaoRequestDTO kakaoRequest = kakaoService.getKakaoUserInfo(authentication);
        return kakaoService.createAddUserForKakao(kakaoRequest);
    }

    // 카카오 로그인 처리
    @GetMapping("login")
    public String kakaoLogin(OAuth2AuthenticationToken authentication, HttpSession session, Model model) {
        try {
            KakaoRequestDTO kakaoUserInfo = kakaoService.getKakaoUserInfo(authentication);

            String kakaoLoginImageUrl = s3Service.getFileUrl("common/kakao_login_medium_narrow.png");
            model.addAttribute("kakaoLoginImageUrl", kakaoLoginImageUrl);

            return kakaoService.LoginForKaKao(kakaoUserInfo, session, model, s3Service);
        } catch (Exception e) {
            logger.error("카카오 로그인 처리 중 오류 발생", e);
            model.addAttribute("error", "카카오 로그인 중 문제가 발생했습니다. 다시 시도해주세요.");
            return "user/login";
        }
    }
}