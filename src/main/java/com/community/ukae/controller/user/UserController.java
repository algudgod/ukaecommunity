package com.community.ukae.controller.user;

import com.community.ukae.dto.user.UserRequestDTO;
import com.community.ukae.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 사용자 등록 form
    @GetMapping("addUserForm")
    public String addUserForm() {
        return "user/addUserForm";
    }

    // 사용자 등록
    @PostMapping("addUser")
    public String addUser(UserRequestDTO userRequest, Model model){
        userService.addUser(userRequest);
        model.addAttribute("user",userRequest); // 환영 메세지에 필요한 필드
        return "user/addUser";
    }
}
