package modu.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import modu.menu.IntegrationTestSupporter;
import modu.menu.domain.Choice;
import modu.menu.domain.Food;
import modu.menu.domain.FoodType;
import modu.menu.repository.FoodRepository;
import modu.menu.controller.model.CategoryResponse;
import modu.menu.controller.model.SearchPlaceResponse;
import modu.menu.domain.Place;
import modu.menu.repository.PlaceRepository;
import modu.menu.domain.PlaceFood;
import modu.menu.repository.PlaceFoodRepository;
import modu.menu.domain.PlaceVibe;
import modu.menu.repository.PlaceVibeRepository;
import modu.menu.domain.Gender;
import modu.menu.domain.User;
import modu.menu.domain.UserStatus;
import modu.menu.domain.Vibe;
import modu.menu.domain.VibeType;
import modu.menu.repository.VibeRepository;
import modu.menu.domain.Vote;
import modu.menu.domain.VoteStatus;
import modu.menu.domain.VoteItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class PlaceServiceTest extends IntegrationTestSupporter {

    @Autowired
    private PlaceService placeService;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private VibeRepository vibeRepository;
    @Autowired
    private PlaceVibeRepository placeVibeRepository;
    @Autowired
    private PlaceFoodRepository placeFoodRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private ObjectMapper om;

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

    @DisplayName("카테고리 목록을 조회한다.")
    @Test
    void getCategory() throws JsonProcessingException {
        // given


        // when
        CategoryResponse result = placeService.getCategory();


        // then
        assertThat(result).isNotNull();
        System.out.println(om.writeValueAsString(result));
        assertThat(result.getFoods())
                .extracting("key", "value")
                .containsExactlyInAnyOrder(
                        tuple("KOREAN_FOOD", "한식"),
                        tuple("JAPANESE_FOOD", "일식"),
                        tuple("CHINESE_FOOD", "중식"),
                        tuple("WESTERN_FOOD", "양식"),
                        tuple("ASIAN_FOOD", "아시아음식"),
                        tuple("BAR", "술집"),
                        tuple("BUFFET", "뷔페"),
                        tuple("DESSERT", "디저트")
                );
        assertThat(result.getFoods().get(0).getChildren())
                .extracting("key", "value")
                .containsExactlyInAnyOrder(
                        tuple("KOREAN_SEAFOOD", "해물,생선"),
                        tuple("CONGEE", "죽"),
                        tuple("JOKBAL", "족발, 보쌈"),
                        tuple("HOT_POT", "찌개,전골"),
                        tuple("MEAT", "육류,고기"),
                        tuple("SUNDAE", "순대"),
                        tuple("STREET_FOOD", "분식"),
                        tuple("NOODLE", "면"),
                        tuple("LUNCH_BOX", "도시락"),
                        tuple("KOREAN_CHICKEN", "닭요리"),
                        tuple("INTESTINE", "곱창,막창"),
                        tuple("TEPPAN_YAKI", "철판요리")
                );
        assertThat(result.getVibes())
                .extracting("key", "value")
                .containsExactlyInAnyOrder(
                        tuple("NOISY", "시끌벅적해요"),
                        tuple("TRENDY", "트렌디해요"),
                        tuple("GOOD_SERVICE", "서비스가 좋아요"),
                        tuple("QUIET", "조용해요"),
                        tuple("MODERN", "모던해요"),
                        tuple("NICE_VIEW", "뷰맛집이에요")
                );
    }

    @DisplayName("현재 위도와 경도, 페이지 번호를 통해 가까운 거리 순으로 정렬되어 페이징된 모든 음식점 정보를 조회한다.")
    @Test
    void searchPlace() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;

        // when
        SearchPlaceResponse searchResult = placeService.searchPlace(latitude, longitude, null, null, page);

        // then
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.getResults().get(0))
                .extracting("id", "name", "foods", "address", "distance", "img")
                .containsExactlyInAnyOrder(2L, "맥도날드 상계DT점", List.of(FoodType.LATIN), "address", "373m", "image");
        assertThat(searchResult.getResults().get(0).getVibes())
                .extracting("title")
                .containsExactlyInAnyOrder(VibeType.QUIET.getTitle());
    }

    @DisplayName("음식점 후보를 검색할 때 페이지 번호에 해당하는 페이징 결과가 존재하지 않을 경우 null을 반환한다.")
    @Test
    void searchPlaceByExceededPageNumber() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 5;

        // when
        SearchPlaceResponse searchResult = placeService.searchPlace(latitude, longitude, null, null, page);

        // then
        assertThat(searchResult).isNull();
    }

    @DisplayName("음식점 후보를 검색할 때 조건에 해당하는 음식점이 존재하지 않을 경우 페이징 결과가 존재하지 않을 경우 null을 반환한다.")
    @Test
    void searchPlaceByNotExistCondition() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 1;
        List<FoodType> foods = List.of(FoodType.LUNCH_BOX);
        List<VibeType> vibes = List.of(VibeType.MODERN);

        // when
        SearchPlaceResponse searchResult = placeService.searchPlace(latitude, longitude, foods, vibes, page);

        // then
        assertThat(searchResult).isNull();
    }

    @DisplayName("현재 위도와 경도, 페이지 번호, 음식 조건들을 통해 가까운 거리 순으로 정렬되어 페이징된 모든 음식점 정보를 조회한다.")
    @Test
    void searchPlaceByFoodTypes() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;

        // when
        SearchPlaceResponse searchResult = placeService.searchPlace(latitude, longitude, List.of(FoodType.LATIN), null, page);

        // then
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.getResults().get(0))
                .extracting("id", "name", "foods", "address", "distance", "img")
                .containsExactlyInAnyOrder(2L, "맥도날드 상계DT점", List.of(FoodType.LATIN), "address", "373m", "image");
        assertThat(searchResult.getResults().get(0).getVibes())
                .extracting("title")
                .containsExactlyInAnyOrder(VibeType.QUIET.getTitle());
    }

    @DisplayName("현재 위도와 경도, 페이지 번호, 분위기 조건들을 통해 가까운 거리 순으로 정렬되어 페이징된 모든 음식점 정보를 조회한다.")
    @Test
    void searchPlaceByVibeTypes() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;

        // when
        SearchPlaceResponse searchResult = placeService.searchPlace(latitude, longitude, null, List.of(VibeType.QUIET), page);

        // then
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.getResults().get(0))
                .extracting("id", "name", "foods", "address", "distance", "img")
                .containsExactlyInAnyOrder(2L, "맥도날드 상계DT점", List.of(FoodType.LATIN), "address", "373m", "image");
        assertThat(searchResult.getResults().get(0).getVibes())
                .extracting("title")
                .containsExactlyInAnyOrder(VibeType.QUIET.getTitle());
    }

    @DisplayName("현재 위도와 경도, 페이지 번호, 음식 조건, 분위기 조건들을 통해 가까운 거리 순으로 정렬되어 페이징된 모든 음식점 정보를 조회한다.")
    @Test
    void searchPlaceByFoodTypesAndVibeTypes() {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;

        // when
        SearchPlaceResponse searchResult = placeService.searchPlace(latitude, longitude, List.of(FoodType.LATIN), List.of(VibeType.QUIET), page);

        // then
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.getResults().get(0))
                .extracting("id", "name", "foods", "address", "distance", "img")
                .containsExactlyInAnyOrder(2L, "맥도날드 상계DT점", List.of(FoodType.LATIN), "address", "373m", "image");
        assertThat(searchResult.getResults().get(0).getVibes())
                .extracting("title")
                .containsExactlyInAnyOrder(VibeType.QUIET.getTitle());
    }

    private User createUser(String email) {
        return User.builder()
                .email(email)
                .name("name")
                .nickname("nickname")
                .gender(Gender.MALE)
                .age(25)
                .birthday(LocalDate.now())
                .password("test1234")
                .phoneNumber("01012345678")
                .status(UserStatus.ACTIVE)
                .build();
    }

    private Place createPlace(String name) {
        return Place.builder()
                .name(name)
                .address("address")
                .ph("string")
                .businessHours("hours")
                .menu("메뉴")
                .latitude(37.676051439616)
                .longitude(127.05563369603)
                .imageUrl("image")
                .voteItems(new ArrayList<>())
                .placeVibes(new ArrayList<>())
                .placeFoods(new ArrayList<>())
                .build();
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

    private Vote createVote(VoteStatus voteStatus) {
        return Vote.builder()
                .voteStatus(voteStatus)
                .voteItems(new ArrayList<>())
                .build();
    }

    private VoteItem createVoteItem(Vote vote, Place place) {
        return VoteItem.builder()
                .vote(vote)
                .place(place)
                .build();
    }

    private Choice createChoice(VoteItem voteItem, User user) {
        return Choice.builder()
                .voteItem(voteItem)
                .user(user)
                .build();
    }
}
