package com.community.ukae.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum BoardCategory {
    // 대분류 | 소분류 | 설명
    NOTICE("유쾌 커뮤니티", "공지","유쾌 커뮤니티 공지사항입니다."),

    HOT("전체", "HOT","인기글을 모아보는 공간입니다."),
    EVENT("전체", "이벤트", "유쾌 커뮤니티 이벤트 공간입니다."),

    LIFE("생활", "일상","유쾌한 사람들의 일상 공간입니다."),
    WORK("생활", "직장인","퇴근을 기원하는 직장인 공간입니다."),
    STOCK("생활", "주식","주식 정보 공유 공간입니다."),

    BASEBALL("스포츠", "야구","야구를 사랑(?)하는 사람들의 공간입니다."),
    SOCCER("스포츠", "축구", "축구를 사랑하는 사람들의 공간입니다.");


    private final String mainCategory;  // 대분류
    private final String subCategory;   // 소분류
    private final String description;   // 설명



}