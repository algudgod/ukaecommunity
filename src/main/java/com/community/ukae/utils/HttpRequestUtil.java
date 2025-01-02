package com.community.ukae.utils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

public class HttpRequestUtil {

    // 공통 HTTP 요청 메서드 (기존 Class<T> 버전)
    public static <T> T sendHttpRequest(
            String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.exchange(url, method, requestEntity, responseType);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("HTTP 요청 실패: " + response.getStatusCode());
        }
    }

    // ParameterizedTypeReference 버전 추가
    public static <T> T sendHttpRequest(
            String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.exchange(url, method, requestEntity, responseType);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("HTTP 요청 실패: " + response.getStatusCode());
        }
    }
}