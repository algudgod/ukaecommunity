package com.community.ukae.repository.user;

import com.community.ukae.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // LoginId 중복 여부 확인
    boolean existsByLoginId(String loginId);

}
