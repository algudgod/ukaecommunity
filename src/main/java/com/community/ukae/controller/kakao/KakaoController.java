package com.community.ukae.controller.kakao;

import com.community.ukae.dto.kakao.KakaoRequestDTO;
import com.community.ukae.service.kakao.KakaoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;
    private final Logger logger = LoggerFactory.getLogger(KakaoController.class);

    // 카카오 회원가입 처리
    @GetMapping("/addUser/kakao")
    public ModelAndView addKakaoUser(OAuth2AuthenticationToken authentication) {
        //logger.info("Authentication는 무엇을 가져오나: {}", authentication);

        // 1. 카카오 사용자 정보 가져오기
        KakaoRequestDTO kakaoRequest = kakaoService.getKakaoUserInfo(authentication);

        // 2. 가입 여부 확인 및 적절한 뷰 반환
        boolean isRegistered = kakaoService.isUserAlreadyRegistered(kakaoRequest.getName(), kakaoRequest.getEmail());
        return kakaoService.createModelAndView(isRegistered, kakaoRequest);
    }
}
