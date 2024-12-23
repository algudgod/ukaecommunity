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
    private String profileUrl;

    // JPA 기본 생성자
    public User() {}

    public String getFormattedPhone() {
        if (phone == null || phone.length() < 10) {
            return phone; // null이거나 유효하지 않은 번호 그대로 반환
        }
        return phone.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
    }
}
