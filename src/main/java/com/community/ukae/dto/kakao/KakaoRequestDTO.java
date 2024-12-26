package com.community.ukae.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoRequestDTO {

    private String name;
    private String email;
    private String phone;
}
