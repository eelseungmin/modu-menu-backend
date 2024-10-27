package modu.menu.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum FoodType {

    // 한식
    KOREAN_FOOD("한식", null),
    KOREAN_SEAFOOD("해물,생선", KOREAN_FOOD),
    CONGEE("죽", KOREAN_FOOD),
    JOKBAL("족발, 보쌈", KOREAN_FOOD),
    HOT_POT("찌개,전골", KOREAN_FOOD),
    MEAT("육류,고기", KOREAN_FOOD),
    SUNDAE("순대", KOREAN_FOOD),
    STREET_FOOD("분식", KOREAN_FOOD),
    NOODLE("면", KOREAN_FOOD),
    LUNCH_BOX("도시락", KOREAN_FOOD),
    KOREAN_CHICKEN("닭요리", KOREAN_FOOD),
    INTESTINE("곱창,막창", KOREAN_FOOD),
    TEPPAN_YAKI("철판요리", KOREAN_FOOD),

    // 일식, 중식
    JAPANESE_FOOD("일식", null),
    CHINESE_FOOD("중식", null),

    // 양식
    WESTERN_FOOD("양식", null),
    HAMBURGER("햄버거", WESTERN_FOOD),
    WESTERN_SEAFOOD("해산물", WESTERN_FOOD),
    PIZZA("피자", WESTERN_FOOD),
    FRENCH_FOOD("프랑스음식", WESTERN_FOOD),
    FAST_FOOD("패스트푸드", WESTERN_FOOD),
    FAMILY_RESTAURANT("패밀리레스토랑", WESTERN_FOOD),
    CHICKEN("치킨", WESTERN_FOOD),
    ITALIAN_FOOD("이탈리안", WESTERN_FOOD),
    SPANISH_FOOD("스페인", WESTERN_FOOD),
    SALAD("샐러드", WESTERN_FOOD),
    LATIN("멕시칸,브라질", WESTERN_FOOD),

    // 기타
    ASIAN_FOOD("아시아음식", null),
    BAR("술집", null),
    BUFFET("뷔페", null),
    DESSERT("디저트", null);

    private final String title;
    @Getter
    private final FoodType parent;

    @JsonValue
    public String getTitle() {
        return title;
    }

    // 최상위 Enum 목록을 반환하는 메서드
    public static List<FoodType> getAncestor() {
        return Arrays.stream(FoodType.values())
                .filter(foodType -> foodType.getParent() == null)
                .toList();
    }
}
