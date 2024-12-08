package com.community.ukae.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    @Column(name = "login_id", nullable = false, length = 20)
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

}
