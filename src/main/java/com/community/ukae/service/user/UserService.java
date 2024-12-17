package com.community.ukae.service.user;

import com.community.ukae.dto.user.UserRequestDTO;
import com.community.ukae.entity.user.User;
import com.community.ukae.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
        user.setPassword(userRequest.getPassword());
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

}
