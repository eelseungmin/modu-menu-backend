package modu.menu.service;

import lombok.RequiredArgsConstructor;
import modu.menu.core.util.DistanceCalculator;
import modu.menu.domain.FoodType;
import modu.menu.controller.model.CategoryResponse;
import modu.menu.controller.model.SearchPlaceResponse;
import modu.menu.domain.Place;
import modu.menu.repository.PlaceQuerydslRepositoryImpl;
import modu.menu.repository.PlaceRepository;
import modu.menu.service.model.FoodTypeServiceResponse;
import modu.menu.service.model.SearchResultServiceResponse;
import modu.menu.service.model.VibeTypeServiceResponse;
import modu.menu.domain.VibeType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

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

        if (places == null || places.getContent().isEmpty()) {
            return null;
        }

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
        for (int i = 0; i < 500000; i++) {
            dummyPlaces.add(
                    Place.builder()
                            .name("dummy" + i)
                            .address("dummy" + i)
                            .ph("dummy" + i)
                            .businessHours("dummy" + i)
                            .menu("dummy" + i)
                            .latitude(37.52519 + 0.00001 * i)
                            .longitude(127.02753 + 0.00001 * i)
                            .imageUrl("dummy" + i)
                            .build()
            );
        }

        placeRepository.insertDummyData(dummyPlaces);
//        placeRepository.saveAll(dummyPlaces);
    }
}