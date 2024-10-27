package modu.menu.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FoodTypeTest {

    @DisplayName("FoodType의 최상위 Enum 목록을 반환한다.")
    @Test
    void getTopLevelCategories() {
        // given

        // when
        List<FoodType> result = FoodType.getAncestor();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result)
                .extracting(Enum::name)
                .containsExactlyInAnyOrder(
                        FoodType.KOREAN_FOOD.name(),
                        FoodType.WESTERN_FOOD.name(),
                        FoodType.CHINESE_FOOD.name(),
                        FoodType.JAPANESE_FOOD.name(),
                        FoodType.ASIAN_FOOD.name(),
                        FoodType.BAR.name(),
                        FoodType.BUFFET.name(),
                        FoodType.DESSERT.name()
                )
                .doesNotContain(FoodType.CHICKEN.name());
    }
}