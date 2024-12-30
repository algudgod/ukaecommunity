package com.community.ukae.controller.home;

import com.community.ukae.entity.user.User;
import com.community.ukae.enums.BoardCategory;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // BoardCategory를 대분류 기준으로 그룹화
        Map<String, List<BoardCategory>> groupedCategories = Arrays.stream(BoardCategory.values())
                .collect(Collectors.groupingBy(BoardCategory::getMainCategory));

        model.addAttribute("groupedCategories", groupedCategories); // 뷰로 전달
        return "home"; // home.html 렌더링
    }
}