package com.community.ukae.entity.user;

import com.community.ukae.dto.UserDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class User {

    @Id
    private String loginId;
    @Column(insertable = false) // default: Y
    private String useYn;
    private String password;
    private String name;
    @Column(unique = true)
    private String nickname;
    @Column(unique = true)
    private String phone;
    @Column(insertable = false) // default: USER
    private String role;
    private String gender;
    @Column(unique = true)
    private String addr;
    private String email;
    @Column(updatable = false, insertable = false)
    private LocalDateTime createDate;
    private LocalDateTime withdrawDate;

    // JPA 기본 생성자
    public User() {}

    // DTO → 엔티티 변환을 위한 생성자
    public User(UserDto userDto) {
        this.loginId = userDto.getLoginId();
        this.password = userDto.getPassword();
        this.email = userDto.getEmail();
        this.name = userDto.getName();
        this.nickname = userDto.getNickname();
        this.phone = userDto.getPhone();
    }

}
