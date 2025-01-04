package com.community.ukae.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardTag {

    TALK_INFO("잡담", BoardCategory.INFO),
    NEWS_INFO("기사/뉴스", BoardCategory.INFO),
    TALK_BASEBALL("잡담", BoardCategory.BASEBALL),
    NEWS_BASEBALL("기사/뉴스", BoardCategory.BASEBALL),
    TALK_STOCK("잡담", BoardCategory.STOCK);

    private final String tagName;
    private final BoardCategory boardCategory;

    // 태그 변환 유틸리티 메서드
    public static String getTagNameOrDefault(String tag) {
        try {
            return BoardTag.valueOf(tag).getTagName();
        } catch (IllegalArgumentException | NullPointerException e) {
            return " ";
        }
    }


}
