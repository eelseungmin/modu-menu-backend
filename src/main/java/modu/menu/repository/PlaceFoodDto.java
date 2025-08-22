package modu.menu.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import modu.menu.domain.FoodType;
import modu.menu.domain.VibeType;

@Getter
@AllArgsConstructor
public class PlaceFoodDto {

    private Long placeId;
    private String name;
    private String address;
    private String imageUrl;
    private FoodType foodType;
}
