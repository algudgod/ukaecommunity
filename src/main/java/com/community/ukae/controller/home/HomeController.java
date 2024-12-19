package com.community.ukae.controller.home;

import com.community.ukae.entity.user.User;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping
    public String home(HttpSession session, Model model) {

        User user = (User)session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "home";
    }
}