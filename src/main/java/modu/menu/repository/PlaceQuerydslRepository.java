package modu.menu.repository;

import modu.menu.domain.FoodType;
import modu.menu.domain.VibeType;
import modu.menu.service.model.PlaceFlatDto;
import modu.menu.service.model.PlaceVibeDto;

import java.util.List;

public interface PlaceQuerydslRepository {

    List<PlaceFlatDto> findByCondition(Double latitude, Double longitude, List<FoodType> foods, List<VibeType> vibes);

    List<PlaceVibeDto> findAllVibesByPlaceIds(List<Long> placeIds);

    List<PlaceFoodDto> findAllFoodsByPlaceIds(List<Long> placeIds);

}
