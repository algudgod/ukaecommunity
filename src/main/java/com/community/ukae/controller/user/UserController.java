package com.community.ukae.controller.user;

import com.community.ukae.dto.user.UserRequestDTO;
import com.community.ukae.entity.user.User;
import com.community.ukae.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 등록 form
    @GetMapping("addUserForm")
    public String addUserForm() {
        return "user/addUserForm";
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
    public String login() {
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

    // 회원 정보
    @PostMapping("updateUserInfo")
    public String updateUserInfo(@ModelAttribute UserRequestDTO userRequest,
                                 @RequestParam String addressDetail,
                                 HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        userService.updateUserInfo(user, userRequest, addressDetail);
        return "redirect:/user/userInfo";
    }
}

