package com.community.ukae.controller.login;

import com.community.ukae.dto.kakao.KakaoRequestDTO;
import com.community.ukae.service.login.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;


@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class KakaoLoginRestController {

    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/kakao")
    public ModelAndView kakaoLogin(@RequestParam(value = "code", required = false) String code) {

        if (code == null) {
            String kakaoAuthUrl = kakaoLoginService.getKakaoAuthUrl();
            return new ModelAndView("redirect:" + kakaoAuthUrl); // 카카오 로그인 페이지로 리다이렉트
        }
        // 1. 인증 코드로 액세스 토큰 요청
        String accessToken = kakaoLoginService.getKakaoAccessToken(code);
        // 2. 액세스 토큰으로 사용자 정보 요청
        Map<String, Object> userInfo = kakaoLoginService.getKakaoUserInfo(accessToken);
        // 3. 사용자 정보를 KakaoRequestDTO로 매핑
        KakaoRequestDTO kakaoRequest = kakaoLoginService.mapToKakaoRequestDTO(userInfo);
        // 3. 가입 여부 확인
        boolean isRegistered = kakaoLoginService.isUserAlreadyRegistered(kakaoRequest.getName(), kakaoRequest.getEmail());

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