package com.community.ukae.service.user;

import com.community.ukae.dto.user.UserRequestDTO;
import com.community.ukae.entity.user.User;
import com.community.ukae.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Mockito 확장 적용
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    //@Test
    void testAddUser() {

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setLoginId("testUser");
        userRequestDTO.setPassword("password123");
        userRequestDTO.setName("테스트 유저");
        userRequestDTO.setEmail("test@domain.com");
        userRequestDTO.setNickname("nickname");
        userRequestDTO.setPhone("01012345678");
        userRequestDTO.setGender("M");
        userRequestDTO.setAddress("서울시 강남구");
        userRequestDTO.setAddressDetail("상세주소");
        userRequestDTO.setAddressExtra("비고");

        userService.addUser(userRequestDTO);

        verify(userRepository).save(any(User.class));
    }

    //@Test
    void testPasswordEncryption() {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("암호화된 비밀번호: " + encodedPassword); // 출력

        assertNotEquals(rawPassword, encodedPassword); // 원본 비밀번호와 암호화된 값이 다른지 확인
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword)); // 비밀번호 매칭 확인
    }

    @Test
    void login() {
    }
}
