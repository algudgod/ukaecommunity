package com.community.ukae.enums;

import java.util.Arrays;
import java.util.List;

public enum BoardCategory {
    NOTICE("유쾌커뮤니티", List.of("공지")),
    ALLBOARD("전체", Arrays.asList("HOT","이벤트")),
    LIFE("생활",Arrays.asList("일상","직장인","주식")),
    SPORTS("스포츠", Arrays.asList("야구","축구"));

    private final String displayName;
    private final List<String> subCategories;


    BoardCategory(String displayName, List<String> subCategories) {
        this.displayName = displayName;
        this.subCategories = subCategories;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }
}