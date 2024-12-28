package com.community.ukae.controller.user;

import com.community.ukae.entity.user.User;
import com.community.ukae.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Map<String, Boolean>> checkLoginId(@RequestBody Map<String, String> request) {

        String loginId = request.get("loginId");
        boolean isAvailableLoginId = userService.checkLoginId(loginId);
        return ResponseEntity.ok(Map.of("isAvailableLoginId", isAvailableLoginId));
    }

    // nickname 중복 확인
    @PostMapping("checkNickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestBody Map<String, String> request) {
        String nickname = request.get("nickname");
        boolean isAvailableNickname = userService.checkNickname(nickname);
        return ResponseEntity.ok(Map.of("isAvailableNickname", isAvailableNickname));
    }

    // email 중복 확인
    @PostMapping("checkEmail")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isAvailableEmail = userService.checkEmail(email);
        return ResponseEntity.ok(Map.of("isAvailableEmail", isAvailableEmail));
    }

    // 아이디 찾기
    @PostMapping("findUserId")
    public ResponseEntity<String> findUserId(@RequestBody Map<String, String> request) {

        String name = request.get("name");
        String email = request.get("email");

        if (name == null || email == null) {
            return ResponseEntity.badRequest().body("이름과 이메일을 모두 입력해주세요.");
        }

        try {
            String maskedUserId = userService.findUserIdByNameAndEmail(name, email);
            return ResponseEntity.ok(maskedUserId); // 성공 시 마스킹된 아이디 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // 비밀번호 찾기
    @PostMapping("findUserPassword")
    public ResponseEntity<String> findUserPassword(@RequestBody Map<String, String> request) {
        String loginId = request.get("loginId");
        String email = request.get("email");

        if (loginId == null || email == null) {
            return ResponseEntity.badRequest().body("아이디와 이메일을 모두 입력해주세요.");
        }
        try {
            userService.findUserPasswordByLoginIdAndEmail(loginId, email);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // 임시 비밀번호 발급
    @PostMapping("sendTemporaryPassword")
    public ResponseEntity<String> sendTempPassword(@RequestBody Map<String, String> request) {
        String loginId = request.get("loginId");
        String email = request.get("email");

        try {
            userService.makeAndSendTempPassword(loginId, email);
            return ResponseEntity.ok("임시 비밀번호가 발송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 비밀번호 변경
    @PostMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> request, HttpSession session) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        // 세션에서 User 객체 가져오기
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // 비밀번호 업데이트 서비스 호출
            userService.updatePassword(user.getLoginId(), currentPassword, newPassword);

            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }



}