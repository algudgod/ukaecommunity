package com.community.ukae.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 5, max = 20, message = "아이디는 5자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^[a-z][a-z0-9]*$")
    private String loginId;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).*$",
                message = "비밀번호는 영문자와 특수문자를 반드시 포함해야 합니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    private String password;
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;
    @NotBlank(message = "별명은 필수 입력 값입니다.")
    private String nickname;
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "유효한 전화번호를 입력하세요.")
    @NotBlank(message = "핸드폰 번호는 필수 입력 값입니다.")
    private String phone;

    private String gender;

    private String postcode;
    private String address;
    private String addressDetail;
    private String addressExtra;

}
