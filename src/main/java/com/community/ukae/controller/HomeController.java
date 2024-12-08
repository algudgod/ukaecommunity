package com.community.ukae.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home() {
        System.out.println("시작해볼까!");
        return "Welcome to the Community!";
    }
}