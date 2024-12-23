package com.community.ukae.service.user;

import com.community.ukae.dto.user.UserRequestDTO;
import com.community.ukae.dto.user.UserUpdateDTO;
import com.community.ukae.entity.user.User;
import com.community.ukae.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화

    // 중복 여부 확인
    public boolean checkLoginId(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }
    public boolean checkNickname(String nickname){
        return !userRepository.existsByNickname(nickname);
    }
    public boolean checkEmail(String email){
        return !userRepository.existsByEmail(email);
    }

    // 회원 등록
    public void addUser(UserRequestDTO userRequest){

        User user = new User();
        user.setLoginId(userRequest.getLoginId());

        String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(encryptedPassword);

        user.setName(userRequest.getName());
        user.setNickname(userRequest.getNickname());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setGender(userRequest.getGender());

        String fullAddress = userRequest.getAddress()+ " " +
                            userRequest.getAddressDetail()+ " " +
                            userRequest.getAddressExtra();
        user.setAddress(fullAddress);

        userRepository.save(user);
    }

    // 회원 로그인
    public User login(String loginId, String password) {

        // 1. 아이디로 사용자 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 3. 아이디 사용 여부 검증
        if ("N".equals(user.getUseYn())) {
            throw new IllegalArgumentException("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }
        // 4. 로그인 성공 시 사용자 반환
        return user;
    }

    // 회원 정보 수정
    public void updateUserInfo(User user, UserUpdateDTO userUpdate, String addressDetail) {

        user.setNickname(userUpdate.getNickname());
        user.setGender(userUpdate.getGender());
        user.setPhone(userUpdate.getPhone());
        String fullAddress = userUpdate.getAddress() + " " + addressDetail;
        user.setAddress(fullAddress);

        // 데이터 저장
        userRepository.save(user);
    }

    // 회원 정보 사용여부 변경 (탈퇴)
    public void deleteUser(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if ("N".equals(user.getUseYn())) {
            throw new IllegalArgumentException("이미 탈퇴한 사용자입니다.");
        }

        user.setUseYn("N");
        user.setWithdrawDate(LocalDateTime.now());
        userRepository.save(user);


    }
}
