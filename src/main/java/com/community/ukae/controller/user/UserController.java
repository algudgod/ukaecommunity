package com.community.ukae.controller.user;

import com.community.ukae.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    //사용자 등록
}
