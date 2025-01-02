package com.community.ukae.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlEncodeUtil {

    private UrlEncodeUtil() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL 인코딩 실패", e);
        }
    }
}


