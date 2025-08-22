package modu.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import modu.menu.core.util.DistanceCalculator;
import modu.menu.domain.*;
import modu.menu.controller.model.CategoryResponse;
import modu.menu.controller.model.SearchPlaceResponse;
import modu.menu.repository.PlaceFoodDto;
import modu.menu.repository.PlaceFoodRepository;
import modu.menu.repository.PlaceRepository;
import modu.menu.repository.PlaceVibeRepository;
import modu.menu.service.model.*;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PlaceService {

    private static final int PAGE_SIZE = 20;
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
        log.debug("======================query start.======================");
        List<PlaceFlatDto> flatList = placeRepository.findByCondition(latitude, longitude, foods, vibes);
        log.debug("쿼리 실행 개수: {}, 쿼리 실행 시간: {}ms", QueryCountHolder.get("ProxyDataSource").getTotal(), QueryCountHolder.get("ProxyDataSource").getTime());
        log.debug("list size: {}", flatList.size());
        log.debug("======================query end.======================");

        // PlaceId 기준으로 그룹핑
        long startTime = System.currentTimeMillis();
        log.debug("======================grouping start.======================");
        Map<Long, SearchResultServiceResponse> placeMap = new HashMap<>();

        for (PlaceFlatDto dto : flatList) {
            SearchResultServiceResponse existing = placeMap.get(dto.getId());

            if (existing == null) {
                double distance = DistanceCalculator.calculate(
                        latitude, longitude,
                        dto.getLatitude(), dto.getLongitude()
                );

                existing = SearchResultServiceResponse.builder()
                        .id(dto.getId())
                        .distance(distance >= 1000.0
                                ? String.format("%.1fkm", distance / 1000.0)
                                : Math.round(distance) + "m")
                        .foods(new ArrayList<>())
                        .vibes(new ArrayList<>())
                        .build();

                placeMap.put(dto.getId(), existing);
            }
        }
        log.debug("그룹핑 시간: {}ms", System.currentTimeMillis() - startTime);
        log.debug("map size: {}", placeMap.size());
        log.debug("======================grouping end.======================");

        // 거리순 정렬
        List<SearchResultServiceResponse> resultList = new ArrayList<>(placeMap.values());
        resultList.sort(Comparator
                .comparing(SearchResultServiceResponse::getDistance)
        );

        // 페이지네이션
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, resultList.size());
        List<SearchResultServiceResponse> pageContent = resultList.subList(start, end);

        // 페이징된 결과의 PlaceId 추출
        List<Long> pageContentPlaceIds = pageContent.stream()
                .map(SearchResultServiceResponse::getId)
                .toList();

        // PlaceId에 대응되는 Vibe 조회
        log.debug("======================query start.======================");
        List<PlaceVibeDto> pageContentVibes = placeRepository.findAllVibesByPlaceIds(pageContentPlaceIds);
        List<PlaceFoodDto> pageContentFoods = placeRepository.findAllFoodsByPlaceIds(pageContentPlaceIds);
        log.debug("쿼리 실행 개수: {}, 쿼리 실행 시간: {}ms", QueryCountHolder.get("ProxyDataSource").getTotal(), QueryCountHolder.get("ProxyDataSource").getTime());
        log.debug("======================query end.======================");

        // 기존 응답에 남은 필드 매핑
        for (SearchResultServiceResponse response : pageContent) {
            for (PlaceVibeDto vibeDto : pageContentVibes) {
                if (vibeDto != null && vibeDto.getPlaceId().equals(response.getId())) {
                    response.getVibes().add(vibeDto.getVibeType());
                }
            }
            for (PlaceFoodDto foodDto : pageContentFoods) {
                if (foodDto != null && foodDto.getPlaceId().equals(response.getId())) {
                    response.getFoods().add(foodDto.getFoodType());
                    response.setName(foodDto.getName());
                    response.setAddress(foodDto.getAddress());
                    response.setImageUrl(foodDto.getImageUrl());
                }
            }
        }

        // 마지막으로 거리순, 이름순 정렬
        pageContent.sort(Comparator
                .comparing(SearchResultServiceResponse::getDistance)
                .thenComparing(SearchResultServiceResponse::getName)
        );

        log.debug("======================making response start.======================");
        return SearchPlaceResponse.builder()
                .results(pageContent)
                .totalElements(resultList.size())
                .totalPages((int) Math.ceil(resultList.size() / (double) PAGE_SIZE))
                .currentPageNumber(page)
                .isFirst(page == 0)
                .isLast(end >= resultList.size())
                .isEmpty(resultList.isEmpty())
                .build();
    }

    @Transactional
    public void insertDummyData() {
        List<Place> dummyPlaces = new ArrayList<>();
        List<PlaceFood> dummyPlaceFoods = new ArrayList<>();
        List<PlaceVibe> dummyPlaceVibes = new ArrayList<>();
        int foodCount = (int) Arrays.stream(FoodType.values()).count();
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
            dummyPlaceFoods.add(PlaceFood.builder()
                    .place(place)
                    .food(Food.builder()
                            .id((long) randFoodNumber + 1)
                            .type(FoodType.values()[randFoodNumber])
                            .build())
                    .build());
            for (VibeType value : VibeType.values()) {
                dummyPlaceVibes.add(PlaceVibe.builder()
                        .place(place)
                        .vibe(Vibe.builder()
                                .id((long) (value.ordinal() + 1))
                                .type(value)
                                .build())
                        .build());
            }
        }

        placeRepository.insertDummyData(dummyPlaces);
        placeFoodRepository.insertDummyData(dummyPlaceFoods);
        placeVibeRepository.insertDummyData(dummyPlaceVibes);
//        placeRepository.saveAll(dummyPlaces);
    }
}