package com.community.ukae.service.user;

import com.community.ukae.dto.UserDto;
import com.community.ukae.entity.user.User;
import com.community.ukae.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean checkLoginId(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }



}
