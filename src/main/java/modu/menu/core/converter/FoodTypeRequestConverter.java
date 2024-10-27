package modu.menu.core.converter;

import modu.menu.domain.FoodType;
import org.springframework.core.convert.converter.Converter;

public class FoodTypeRequestConverter implements Converter<String, FoodType> {

    @Override
    public FoodType convert(String source) {
        return FoodType.valueOf(source.toUpperCase());
    }
}
