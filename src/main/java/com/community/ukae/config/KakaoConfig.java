package com.community.ukae.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kakao")
@Data
public class KakaoConfig {

    private String restApiKey;
    private String redirectUri;
}
