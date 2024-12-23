package com.community.ukae.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotBlank(message = "별명은 필수 입력 값입니다.")
    private String nickname;
    @NotBlank(message = "성별은 필수 입력 값입니다.")
    private String gender;
    private String address;
    private String addressDetail;
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "유효한 전화번호를 입력하세요.")
    @NotBlank(message = "핸드폰 번호는 필수 입력 값입니다.")
    private String phone;

}
