package com.community.ukae.controller.kakao;

import com.community.ukae.dto.kakao.KakaoRequestDTO;
import com.community.ukae.entity.user.User;
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
        //logger.info("Authentication는 무엇을 가져오나: {}", authentication);

        // 1. 카카오 사용자 정보 가져오기
        KakaoRequestDTO kakaoRequest = kakaoService.getKakaoUserInfo(authentication);

        // 2. 가입 여부 확인 및 적절한 뷰 반환
        boolean isRegistered = kakaoService.isUserAlreadyRegistered(kakaoRequest.getName(), kakaoRequest.getEmail());
        return kakaoService.createModelAndView(isRegistered, kakaoRequest);
    }

    @GetMapping("login")
    public String  kakaoLogin(OAuth2AuthenticationToken authentication, HttpSession session, Model model) {
        try {
            // 1. 카카오 사용자 정보 가져오기
            KakaoRequestDTO kakaoUserInfo = kakaoService.getKakaoUserInfo(authentication);

            // 2. 가입 여부 확인
            boolean isRegistered = kakaoService.isUserAlreadyRegistered(kakaoUserInfo.getName(), kakaoUserInfo.getEmail());
            if (isRegistered) {
                logger.info("기존 사용자 로그인 성공!");
                logger.info("기존 사용자 전체 정보: {}", kakaoUserInfo);

                User user = kakaoService.findByNameAndEmail(kakaoUserInfo.getName(), kakaoUserInfo.getEmail());
                // 3. 로그인 처리 - 세션에 사용자 정보 저장
                session.setAttribute("user", user);
                return "redirect:/";

            } else {
                logger.warn("미가입 사용자입니다.");

                // 4. 미가입 사용자 처리 - 회원가입 페이지로 리다이렉트
                String kakaoLoginImageUrl = s3Service.getFileUrl("kakao_login_medium_narrow.png");
                model.addAttribute("kakaoLoginImageUrl", kakaoLoginImageUrl); // 이미지 다시 설정
                model.addAttribute("error", "가입된 정보가 없습니다.");
                return "user/login";
            }
        } catch (Exception e) {
            logger.error("카카오 로그인 처리 중 오류 발생", e);
            model.addAttribute("error", "카카오 로그인 중 문제가 발생했습니다. 다시 시도해주세요.");
            return "user/login";
        }
    }

}
