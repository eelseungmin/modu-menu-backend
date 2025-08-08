package modu.menu.repository;

import modu.menu.IntegrationTestSupporter;
import modu.menu.domain.Food;
import modu.menu.domain.FoodType;
import modu.menu.domain.Place;
import modu.menu.domain.PlaceFood;
import modu.menu.domain.PlaceVibe;
import modu.menu.domain.Vibe;
import modu.menu.domain.VibeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlaceRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private VibeRepository vibeRepository;

    @Autowired
    private PlaceVibeRepository placeVibeRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private PlaceFoodRepository placeFoodRepository;

    @Autowired
    private PlaceQuerydslRepositoryImpl placeQuerydslRepositoryImpl;

    @BeforeEach
    void setUp() {
        Place place1 = createPlaceByLatitudeAndLongitude("타코벨", 37.5251923, 127.027536);
        Place place2 = createPlaceByLatitudeAndLongitude("맥도날드 상계DT점", 37.6723375, 127.0562151);
        Place place3 = createPlaceByLatitudeAndLongitude("롯데시네마 수락산", 37.6737992, 127.0556511);
        Vibe vibe1 = createVibe(VibeType.NOISY);
        Vibe vibe2 = createVibe(VibeType.QUIET);
        Vibe vibe3 = createVibe(VibeType.GOOD_SERVICE);
        PlaceVibe placeVibe1 = createPlaceVibe(place1, vibe1);
        place1.addPlaceVibe(placeVibe1);
        PlaceVibe placeVibe2 = createPlaceVibe(place2, vibe2);
        place2.addPlaceVibe(placeVibe2);
        PlaceVibe placeVibe3 = createPlaceVibe(place3, vibe3);
        place3.addPlaceVibe(placeVibe3);
        placeRepository.saveAll(List.of(place1, place2, place3));
        vibeRepository.saveAll(List.of(vibe1, vibe2, vibe3));
        placeVibeRepository.saveAll(List.of(placeVibe1, placeVibe2, placeVibe3));

        Food food1 = createFood(FoodType.LATIN);
        Food food2 = createFood(FoodType.MEAT);
        PlaceFood placeFood1 = createPlaceFood(place1, food1);
        place1.addPlaceFood(placeFood1);
        PlaceFood placeFood2 = createPlaceFood(place2, food1);
        place2.addPlaceFood(placeFood2);
        PlaceFood placeFood3 = createPlaceFood(place3, food2);
        place3.addPlaceFood(placeFood3);
        foodRepository.saveAll(List.of(food1, food2));
        placeFoodRepository.saveAll(List.of(placeFood1, placeFood2, placeFood3));
    }

    @DisplayName("현재 위도와 경도, 페이지 번호를 통해 가까운 거리 순으로 정렬되어 페이징된 모든 음식점 정보를 조회한다.")
    @Test
    void findAll() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;

        // when
        Page<Place> places = placeQuerydslRepositoryImpl.findByCondition(latitude, longitude, null, null, page);

        // then
        assertThat(places.getContent()).hasSize(2);
        assertThat(places.getContent().get(0).getName()).isEqualTo("맥도날드 상계DT점");
        assertThat(places.getContent().get(1).getName()).isEqualTo("롯데시네마 수락산");
    }

    @DisplayName("현재 위도와 경도, 페이지 번호, 분위기 조건들을 통해 가까운 거리 순으로 정렬되어 페이징된 모든 음식점 정보를 조회한다.")
    @Test
    void findByConditionByVibeTypes() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;
        List<VibeType> vibes = List.of(VibeType.QUIET);

        // when
        Page<Place> places = placeQuerydslRepositoryImpl.findByCondition(latitude, longitude, null, vibes, page);

        // then
        assertThat(places.getContent()).hasSize(2);
        assertThat(places.getContent().get(0).getName()).isEqualTo("맥도날드 상계DT점");
    }

    @DisplayName("현재 위도와 경도, 페이지 번호, 음식 조건들을 통해 가까운 거리 순으로 정렬되어 페이징된 모든 음식점 정보를 조회한다.")
    @Test
    void findByConditionByFoodTypes() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;
        List<FoodType> foods = List.of(FoodType.LATIN);

        // when
        Page<Place> places = placeQuerydslRepositoryImpl.findByCondition(latitude, longitude, foods, null, page);

        // then
        assertThat(places.getContent()).hasSize(1);
        assertThat(places.getContent().get(0).getName()).isEqualTo("맥도날드 상계DT점");
    }

    @DisplayName("현재 위도와 경도, 페이지 번호, 분위기와 음식 조건들을 통해 가까운 거리 순으로 정렬되어 페이징된 모든 음식점 정보를 조회한다.")
    @Test
    void findByCondition() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;
        List<FoodType> foods = List.of(FoodType.LATIN);
        List<VibeType> vibes = List.of(VibeType.QUIET);

        // when
        Page<Place> places = placeQuerydslRepositoryImpl.findByCondition(latitude, longitude, foods, vibes, page);

        // then
        assertThat(places.getContent()).hasSize(1);
        assertThat(places.getContent().get(0).getName()).isEqualTo("맥도날드 상계DT점");
    }

    private Place createPlaceByLatitudeAndLongitude(String name, Double latitude, Double longitude) {
        return Place.builder()
                .name(name)
                .address("address")
                .ph("string")
                .businessHours("hours")
                .menu("메뉴")
                .latitude(latitude)
                .longitude(longitude)
                .imageUrl("image")
                .voteItems(new ArrayList<>())
                .placeVibes(new ArrayList<>())
                .placeFoods(new ArrayList<>())
                .build();
    }

    private Vibe createVibe(VibeType type) {
        return Vibe.builder()
                .type(type)
                .build();
    }

    private PlaceVibe createPlaceVibe(Place place, Vibe vibe) {
        return PlaceVibe.builder()
                .place(place)
                .vibe(vibe)
                .build();
    }

    private Food createFood(FoodType type) {
        return Food.builder()
                .type(type)
                .build();
    }

    private PlaceFood createPlaceFood(Place place, Food food) {
        return PlaceFood.builder()
                .place(place)
                .food(food)
                .build();
    }
}
