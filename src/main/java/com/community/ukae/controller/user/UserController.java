package com.community.ukae.controller.user;

import com.community.ukae.dto.kakao.KakaoRequestDTO;
import com.community.ukae.dto.user.UserRequestDTO;
import com.community.ukae.dto.user.UserUpdateDTO;
import com.community.ukae.entity.user.User;
import com.community.ukae.service.s3.S3Service;
import com.community.ukae.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;


    // 회원 등록 form
    @GetMapping("addUserForm")
    public String addUserForm() {
        return "user/addUserForm";
    }

    // 회원 등록 form (카카오)
    @GetMapping("addKakaoUserForm")
    public String addKakaoUserForm(@ModelAttribute KakaoRequestDTO kakaoRequest, Model model) {
        model.addAttribute("kakaoRequest", kakaoRequest); // 모델에 DTO 추가
        return "user/addKakaoUserForm"; // Thymeleaf 템플릿
    }

    // 회원 등록
    @PostMapping("addUser")
    public String addUser(UserRequestDTO userRequest, Model model) {
        userService.addUser(userRequest);
        model.addAttribute("user", userRequest); // 환영 메세지에 필요한 필드
        return "user/addUser";
    }

    // 회원 로그인 form
    @GetMapping("login")
    public String login(Model model) {

        String kakaoLoginImageUrl = s3Service.getFileUrl("kakao_login_medium_narrow.png");
        model.addAttribute("kakaoLoginImageUrl",kakaoLoginImageUrl);
        return "user/login";
    }

    // 회원 로그인
    @PostMapping("login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password, HttpSession session, Model model) {

        try {
            User user = userService.login(loginId, password);

            session.setAttribute("user", user);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/login";
        }
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    // 회원 정보 조회
    @GetMapping("userInfo")
    public String getUserInfo(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);
        return "user/userInfo";
    }

    // 회원 정보 수정 form
    @GetMapping("editUserForm")
    public String editUserInfo(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);
        return "user/editUserForm";
    }

    // 회원 정보 수정
    @PostMapping("updateUserInfo")
    public String updateUserInfo(@ModelAttribute UserUpdateDTO userUpdate,
                                 @RequestParam String addressDetail,
                                 HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        userService.updateUserInfo(user, userUpdate, addressDetail);
        return "redirect:/user/userInfo";
    }

    // 회원 정보 사용여부 변경 (탈퇴)
    @PostMapping("deleteUser")
    public String deleteUser(HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        userService.deleteUser(user.getLoginId());
        session.invalidate();

        return "redirect:/";
    }
}

