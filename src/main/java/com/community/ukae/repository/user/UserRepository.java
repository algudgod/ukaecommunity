package com.community.ukae.repository.user;

import com.community.ukae.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // 중복 여부 확인
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    // 조회
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByNameAndEmail(String name, String email);
    Optional<User> findByLoginIdAndEmail(String loginId, String email);

}
