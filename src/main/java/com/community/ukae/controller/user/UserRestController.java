package com.community.ukae.controller.user;

import com.community.ukae.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    // loginId 중복 확인
    @PostMapping("checkLoginId")
    public ResponseEntity<Map<String, Boolean>> checkLoginId(@RequestBody Map<String, String> request){

        String loginId = request.get("loginId");
        boolean isAvailableLoginId = userService.checkLoginId(loginId);
        return ResponseEntity.ok(Map.of("isAvailableLoginId",isAvailableLoginId));
    }

    // nickname 중복 확인
    @PostMapping("checkNickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestBody Map<String, String> request){
        String nickname = request.get("nickname");
        boolean isAvailableNickname = userService.checkNickname(nickname);
        return ResponseEntity.ok(Map.of("isAvailableNickname",isAvailableNickname));
    }

    // email 중복 확인
    @PostMapping("checkEmail")
    public ResponseEntity<Map<String,Boolean>> checkEmail(@RequestBody Map<String,String>request){
        String email = request.get("email");
        boolean isAvailableEmail = userService.checkEmail(email);
        return ResponseEntity.ok(Map.of("isAvailableEmail",isAvailableEmail));
    }


}
