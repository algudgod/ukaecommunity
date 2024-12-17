package com.community.ukae.entity.user;

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
    private String address;
    @Column(unique = true)
    private String email;
    @Column(updatable = false, insertable = false)
    private LocalDateTime createDate;
    private LocalDateTime withdrawDate;

    // JPA 기본 생성자
    public User() {}

}
