package modu.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import modu.menu.core.util.DistanceCalculator;
import modu.menu.domain.*;
import modu.menu.controller.model.CategoryResponse;
import modu.menu.controller.model.SearchPlaceResponse;
import modu.menu.repository.PlaceFoodRepository;
import modu.menu.repository.PlaceRepository;
import modu.menu.repository.PlaceVibeRepository;
import modu.menu.service.model.FoodTypeServiceResponse;
import modu.menu.service.model.SearchResultServiceResponse;
import modu.menu.service.model.VibeTypeServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceFoodRepository placeFoodRepository;
    private final PlaceVibeRepository placeVibeRepository;

    // 카테고리 목록 조회
    public CategoryResponse getCategory() {
        return CategoryResponse.builder()
                .foods(FoodTypeServiceResponse.getFoodTypeHierarchy())
                .vibes(VibeTypeServiceResponse.toList())
                .build();
    }

    // 음식점 후보 검색
    public SearchPlaceResponse searchPlace(
            Double latitude,
            Double longitude,
            List<FoodType> foods,
            List<VibeType> vibes,
            Integer page
    ) {

        Page<Place> places = placeRepository.findByCondition(latitude, longitude, foods, vibes, page);

        log.debug("======================places size: {}======================", places.getContent().size());
        if (places == null || places.getContent().isEmpty()) {
            return null;
        }

        log.debug("======================build response start.======================");
        return SearchPlaceResponse.builder()
                .results(places.getContent().stream()
                        .map(place -> {
                            double distance = DistanceCalculator.calculate(
                                    latitude,
                                    longitude,
                                    place.getLatitude(),
                                    place.getLongitude()
                            );

                            return SearchResultServiceResponse.builder()
                                    .id(place.getId())
                                    .name(place.getName())
                                    .foods(place.getPlaceFoods().stream()
                                            .map(placeFood -> placeFood.getFood().getType())
                                            .toList())
                                    .vibes(place.getPlaceVibes().stream()
                                            .map(placeVibe -> placeVibe.getVibe().getType())
                                            .toList())
                                    .address(place.getAddress())
                                    .distance(distance >= 1000.0 ? String.format("%.1f", distance / 1000.0) + "km" : Math.round(distance) + "m")
                                    .img(place.getImageUrl())
                                    .build();
                        })
                        .toList())
                .totalElements(places.getTotalElements())
                .totalPages(places.getTotalPages())
                .currentPageNumber(places.getNumber())
                .isFirst(places.isFirst())
                .isLast(places.isLast())
                .isEmpty(places.isEmpty())
                .build();
    }

    @Transactional
    public void insertDummyData() {
        List<Place> dummyPlaces = new ArrayList<>();
        List<PlaceFood> dummyPlaceFoods = new ArrayList<>();
        List<PlaceVibe> dummyPlaceVibes = new ArrayList<>();
        int foodCount = (int) Arrays.stream(FoodType.values()).count();
        int vibeCount = (int) Arrays.stream(VibeType.values()).count();
        Random random = new Random();
        for (int i = 0; i < 1000000; i++) {
            Place place = Place.builder()
                    .id((long) i + 1)
                    .name("dummy" + i)
                    .address("dummy" + i)
                    .ph("dummy" + i)
                    .businessHours("dummy" + i)
                    .menu("dummy" + i)
                    .latitude(37.6545381 + 0.00001 * i)
                    .longitude(127.06056783 + 0.00001 * i)
                    .imageUrl("dummy" + i)
                    .build();
            dummyPlaces.add(place);
            int randFoodNumber = random.nextInt(foodCount);
            int randVibeNumber = random.nextInt(vibeCount);
            dummyPlaceFoods.add(PlaceFood.builder()
                    .place(place)
                    .food(Food.builder()
                            .id((long) randFoodNumber + 1)
                            .type(FoodType.values()[randFoodNumber])
                            .build())
                    .build());
            dummyPlaceVibes.add(PlaceVibe.builder()
                    .place(place)
                    .vibe(Vibe.builder()
                            .id((long) randVibeNumber + 1)
                            .type(VibeType.values()[randVibeNumber])
                            .build())
                    .build());
        }

        placeRepository.insertDummyData(dummyPlaces);
        placeFoodRepository.insertDummyData(dummyPlaceFoods);
        placeVibeRepository.insertDummyData(dummyPlaceVibes);
//        placeRepository.saveAll(dummyPlaces);
    }
}