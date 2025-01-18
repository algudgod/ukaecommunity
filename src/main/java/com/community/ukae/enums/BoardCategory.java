package com.community.ukae.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum BoardCategory {

    // 상수(소분류) | 대분류 | 대분류이름 |소분류이름 | 설명
    NOTICE("COMMUNITY","유쾌 커뮤니티", "공지","유쾌 커뮤니티 공지사항입니다."),

    HOT("ALL_BOARDS","전체", "HOT","인기글을 모아보는 공간입니다."),
    EVENT("ALL_BOARDS","전체", "이벤트", "유쾌 커뮤니티 이벤트 공간입니다."),

    LIFE("ALL_LIFE","생활", "일상","유쾌한 사람들의 일상 공간입니다."),
    WORK("ALL_LIFE","생활", "직장인","퇴근을 기원하는 직장인 공간입니다."),
    STOCK("ALL_LIFE","생활", "주식","주식 정보 공유 공간입니다."),
    INFO("ALL_LIFE","생활","정보/지식","갖가지 정보들을 공유하는 공간입니다."),

    ANIMAL("HOBBY","취미","동물","동물을 사랑하는 집사들의 공간입니다."),
    PLANT("HOBBY","취미","식물","식물을 사랑하는 집사들의 공간입니다. "),
    SOUND("HOBBY","취미", "음향기기","사운드를 사랑하는 음향기기 러버들의 공간입니다."),

    RECIPE("FOOD","음식", "레시피","요리를 사랑하는 사람들의 공간입니다."),
    FOODSPOT("FOOD","음식","맛집","맛집을 추천해주는 공간입니다."),

    BASEBALL("SPORTS","스포츠", "야구","야구를 사랑(?)하는 사람들의 공간입니다."),
    SOCCER("SPORTS","스포츠", "축구", "축구를 사랑하는 사람들의 공간입니다."),

    BOOK("CULTURE","문화","도서","책을 사랑하는 사람들의 공간입니다."),
    MOVIE("CULTURE","문화","영화","영화를 사랑하는 사람들의 공간입니다."),

    ONEPIECE("ANIMATION","애니메이션","원피스","원피스를 사랑하는 해적들의 공간입니다."),
    NARUTO("ANIMATION","애니메이션","나루토","나루토를 사랑하는 닌자들의 공간입니다."),

    ALL_GAMES("GAMES","게임","모든게임","게임을 사랑하는 사람들의 공간입니다."),
    LOL("GAMES","게임","리그오브레전드","리그 오브 레전드를 사랑하는 사람들의 공간입니다."),
    STEAM("GAMES","게임","스팀", "스팀 게임을 사랑하는 사람들의 공간입니다."),

    HARRYPOTTER("MANIA","매니아","해리포터","해리포터를 사랑하는 사람들의 공간입니다."),
    STARWARS("MANIA","매니아","스타워즈","스타워즈를 사랑하는 사람들의 공간입니다."),
    MAVEL("MANIA","매니아","마블","마블을 사랑하는 사람들의 공간입니다."),
    DISNEY("MANIA","매니아","디즈니","디즈니를 사랑하는 사람들의 공간입니다.");


    // Map<String, List<BoardCategory>>
    private final String mainCategory;  // 대분류 key

    private final String mainCateName;  // 대분류 이름 value[0]
    private final String subCateName;   // 소분류 이름 value[1]
    private final String description;   // 설명 value[2]


}