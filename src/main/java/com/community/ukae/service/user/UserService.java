package com.community.ukae.service.user;

import com.community.ukae.dto.email.EmailRequestDTO;
import com.community.ukae.dto.user.UserRequestDTO;
import com.community.ukae.dto.user.UserUpdateDTO;
import com.community.ukae.entity.user.User;
import com.community.ukae.repository.user.UserRepository;
import com.community.ukae.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 중복 여부 확인
    public boolean checkLoginId(String loginId) {
        logger.info("로그인 아이디 중복 확인 요청: loginId={}", loginId);
        return !userRepository.existsByLoginId(loginId);
    }
    public boolean checkNickname(String nickname) {
        logger.info("닉네임 중복 확인 요청: nickname={}", nickname);
        return !userRepository.existsByNickname(nickname);
    }
    public boolean checkEmail(String email) {
        logger.info("이메일 중복 확인 요청: email={}", email);
        return !userRepository.existsByEmail(email);
    }
    public Optional<User> findByNameAndEmail(String name, String email) {
        logger.info("사용자 조회 요청: name={}, email={}", name, email);
        return userRepository.findByNameAndEmail(name, email);
    }

    // 회원 등록
    public void addUser(UserRequestDTO userRequest) {
        logger.info("회원 등록 요청: loginId={}", userRequest.getLoginId());

        User user = new User();
        user.setLoginId(userRequest.getLoginId());

        String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(encryptedPassword);

        user.setName(userRequest.getName());
        user.setNickname(userRequest.getNickname());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setGender(userRequest.getGender());

        String fullAddress = userRequest.getAddress() + " " +
                userRequest.getAddressDetail() + " " +
                userRequest.getAddressExtra();
        user.setAddress(fullAddress);

        userRepository.save(user);
        logger.info("회원 등록 성공: loginId={}", userRequest.getLoginId());

    }

    // 회원 로그인
    public User login(String loginId, String password) {
        logger.info("로그인 요청: loginId={}", loginId);

        // 1. 아이디로 사용자 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    logger.warn("로그인 실패 - 존재하지 않는 아이디: loginId={}", loginId);
                    return new IllegalArgumentException("존재하지 않는 아이디입니다.");
                });
        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("로그인 실패 - 비밀번호 불일치: loginId={}", loginId);
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 3. 아이디 사용 여부 검증
        if ("N".equals(user.getUseYn())) {
            logger.warn("로그인 실패 - 비활성화된 계정: loginId={}", loginId);
            throw new IllegalArgumentException("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }

        logger.info("로그인 성공: loginId={}", loginId);
        return user;
    }

    // 회원 정보 수정
    public void updateUserInfo(User user, UserUpdateDTO userUpdate, String addressDetail) {
        logger.info("회원 정보 수정 요청: userId={}", user.getLoginId());

        user.setNickname(userUpdate.getNickname());
        user.setGender(userUpdate.getGender());
        user.setPhone(userUpdate.getPhone());
        user.setAddress(userUpdate.getAddress() + " " + addressDetail);

        userRepository.save(user);
        logger.info("회원 정보 수정 성공: userId={}", user.getLoginId());
    }

    // 회원 정보 사용여부 변경 (탈퇴)
    public void deleteUser(String loginId) {
        logger.info("회원 탈퇴 요청: loginId={}", loginId);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    logger.warn("회원 탈퇴 실패 - 사용자 없음: loginId={}", loginId);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        if ("N".equals(user.getUseYn())) {
            logger.warn("회원 탈퇴 실패 - 이미 탈퇴한 사용자: loginId={}", loginId);
            throw new IllegalArgumentException("이미 탈퇴한 사용자입니다.");
        }

        user.setUseYn("N");
        user.setWithdrawDate(LocalDateTime.now());
        userRepository.save(user);
        logger.info("회원 탈퇴 성공: loginId={}", loginId);
    }

    // 회원 아이디 찾기
    public String findUserIdByNameAndEmail(String name, String email) {
        logger.info("아이디 찾기 요청: name={}, email={}", name, email);

        User user = userRepository.findByNameAndEmail(name, email)
                .orElseThrow(() -> {
                    logger.warn("아이디 찾기 실패 - 일치하는 계정 없음: name={}, email={}", name, email);
                    return new IllegalArgumentException("일치하는 계정을 찾을 수 없습니다.");
                });

        if (!"Y".equals(user.getUseYn())) {
            logger.warn("아이디 찾기 실패 - 비활성화된 계정: name={}, email={}", name, email);
            throw new IllegalStateException("비활성화된 계정입니다.");
        }

        String maskedUserId = maskUserId(user.getLoginId());
        logger.info("아이디 찾기 성공: name={}, email={}, maskedUserId={}", name, email, maskedUserId);
        return maskedUserId;
    }

    // 아이디 마스킹 처리
    private String maskUserId(String userId) {

        int visibleLength = userId.length() - 4;
        return userId.substring(0, visibleLength) + "****"; // 앞부분 + 뒤쪽 4자리 마스킹
    }

    // 회원 비밀번호 찾기
    public void findUserPasswordByLoginIdAndEmail(String loginId, String email) {
        // 로그인 아이디와 이메일로 사용자 조회
        User user = userRepository.findByLoginIdAndEmail(loginId, email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 계정을 찾을 수 없습니다."));

        // 계정 비활성화 여부 확인
        if (!"Y".equals(user.getUseYn())) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }
    }

    // 임시 비밀번호 발급 (랜덤)
    public void makeAndSendTempPassword(String loginId, String email) {
        logger.info("임시 비밀번호 발급 요청: loginId={}, email={}", loginId, email);

        User user = userRepository.findByLoginIdAndEmail(loginId, email)
                .orElseThrow(() -> {
                    logger.warn("임시 비밀번호 발급 실패 - 사용자 없음: loginId={}, email={}", loginId, email);
                    return new IllegalArgumentException("일치하는 계정을 찾을 수 없습니다.");
                });

        // 임시 비밀번호 생성
        String tempPassword = UUID.randomUUID().toString().substring(0, 6) + "!@";
        // 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // 이메일 발송
        String subject = "유쾌 커뮤니티 임시 비밀번호 발급 이메일입니다.";
        String content = "<p>임시 비밀번호: <strong>" + tempPassword + "</strong></p>" +
                "<p>로그인 후 반드시 비밀번호를 변경해주세요.</p>";
        emailService.sendEmail(new EmailRequestDTO(email, subject, content));

        logger.info("임시 비밀번호 발급 성공: loginId={}, email={}", loginId, email);
    }

    // 비밀번호 수정
    public void updatePassword(String loginId, String currentPassword, String newPassword) {
        logger.info("비밀번호 변경 요청: loginId={}", loginId);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    logger.warn("비밀번호 변경 실패 - 사용자 없음: loginId={}", loginId);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("비밀번호 변경 실패 - 현재 비밀번호 불일치: loginId={}", loginId);
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        // 새 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("비밀번호 변경 성공: loginId={}", loginId);

    }

}
