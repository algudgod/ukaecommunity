package com.community.ukae.controller.error;

import com.community.ukae.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("error")
@RequiredArgsConstructor
public class ErrorController {

    private final S3Service s3Service;

    @GetMapping("/notFound")
    public String notFound(Model model){

        String notFoundUrl = s3Service.getFileUrl("common/error404.png");
        model.addAttribute("notFoundUrl", notFoundUrl);
        return "error/notFound";
    }
}
