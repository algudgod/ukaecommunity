package com.community.ukae.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum BoardCategory {

    NOTICE("공지","유쾌 커뮤니티 공지사항입니다."),
    HOT("HOT","인기글을 모아보는 공간입니다."),
    EVENT("이벤트","유쾌 커뮤니티 이벤트 공간입니다."),
    FREE("일상","유쾌한 사람들의 자유롭게 소통하는 공간입니다."),
    WORK("직장인","퇴근,,,,시켜줘,,,"),
    INFO("주식","유쾌한 사람들의 주식 정보 공유 공간 입니다."),
    BASEBALL("야구", "야구를 사랑(?)하는 사람들의 공간입니다."),
    SOCCER("축구", "축구를 사랑하는 사람들의 공간입니다.");

    private final String displayName;
    private final String description;



}