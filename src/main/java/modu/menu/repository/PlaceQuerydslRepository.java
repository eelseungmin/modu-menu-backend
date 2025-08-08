package modu.menu.repository;

import modu.menu.domain.FoodType;
import modu.menu.domain.Place;
import modu.menu.domain.VibeType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PlaceQuerydslRepository {

    Page<Place> findByCondition(Double latitude, Double longitude, List<FoodType> foods, List<VibeType> vibes, Integer page);

}
