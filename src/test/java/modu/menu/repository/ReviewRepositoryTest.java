package modu.menu.repository;

import modu.menu.IntegrationTestSupporter;
import modu.menu.domain.Choice;
import modu.menu.domain.Food;
import modu.menu.domain.FoodType;
import modu.menu.domain.Place;
import modu.menu.domain.PlaceFood;
import modu.menu.domain.PlaceVibe;
import modu.menu.domain.HasRoom;
import modu.menu.domain.Review;
import modu.menu.domain.ReviewStatus;
import modu.menu.domain.Gender;
import modu.menu.domain.User;
import modu.menu.domain.UserStatus;
import modu.menu.domain.Vibe;
import modu.menu.domain.VibeType;
import modu.menu.domain.Vote;
import modu.menu.domain.VoteStatus;
import modu.menu.domain.VoteItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private UserRepository userRepository;

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
    private VoteRepository voteRepository;

    @Autowired
    private VoteItemRepository voteItemRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @DisplayName("placeId 목록과 userId, VoteStatus를 통해서 사용자가 참여한 투표에 포함된 음식점을 평가한 이력을 조회한다.")
    @Test
    void countByPlaceIdsUserIdAndVoteStatus() {
        // given
        User user1 = createUser("hong1234@naver.com");
        User user2 = createUser("kim1234@naver.com");
        User user3 = createUser("new1234@naver.com");
        Place place1 = createPlace("타코벨");
        Place place2 = createPlace("이자카야모리");
        Place place3 = createPlace("서가앤쿡 노원역점");
        Vibe vibe1 = createVibe(VibeType.NOISY);
        Vibe vibe2 = createVibe(VibeType.QUIET);
        Vibe vibe3 = createVibe(VibeType.GOOD_SERVICE);
        PlaceVibe placeVibe1 = createPlaceVibe(place1, vibe1);
        place1.addPlaceVibe(placeVibe1);
        PlaceVibe placeVibe2 = createPlaceVibe(place2, vibe2);
        place2.addPlaceVibe(placeVibe2);
        PlaceVibe placeVibe3 = createPlaceVibe(place3, vibe3);
        place3.addPlaceVibe(placeVibe3);
        userRepository.saveAll(List.of(user1, user2, user3));
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

        Vote vote = createVote(VoteStatus.END);
        VoteItem voteItem1 = createVoteItem(vote, place1);
        VoteItem voteItem2 = createVoteItem(vote, place2);
        VoteItem voteItem3 = createVoteItem(vote, place3);
        vote.addVoteItem(voteItem1);
        vote.addVoteItem(voteItem2);
        vote.addVoteItem(voteItem3);
        Vote vote2 = createVote(VoteStatus.END);
        VoteItem voteItem4 = createVoteItem(vote2, place1);
        VoteItem voteItem5 = createVoteItem(vote2, place2);
        VoteItem voteItem6 = createVoteItem(vote2, place3);
        vote2.addVoteItem(voteItem4);
        vote2.addVoteItem(voteItem5);
        vote2.addVoteItem(voteItem6);
        Choice choice1 = createChoice(voteItem1, user1);
        Choice choice2 = createChoice(voteItem2, user2);
        Choice choice3 = createChoice(voteItem3, user3);
        voteRepository.saveAll(List.of(vote, vote2));
        voteItemRepository.saveAll(List.of(voteItem1, voteItem2, voteItem3, voteItem4, voteItem5, voteItem6));
        choiceRepository.saveAll(List.of(choice1, choice2, choice3));

        Review review1 = createReview(user1, vote, place1);
        vote.addReview(review1);
        Review review2 = createReview(user1, vote2, place2);
        vote2.addReview(review2);
        reviewRepository.saveAll(List.of(review1, review2));

        Long userId = 1L;
        Long voteId = 1L;
        VoteStatus status = VoteStatus.END;

        // when
        int count = reviewRepository.countByUserIdAndVoteIdAndVoteStatus(userId, voteId, status);

        // then
        assertThat(count)
                .isEqualTo(1);
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
                .latitude(125.00000)
                .longitude(14.12133)
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

    private Review createReview(User user, Vote vote, Place place) {
        return Review.builder()
                .user(user)
                .vote(vote)
                .place(place)
                .rating(3)
                .participants(10)
                .hasRoom(HasRoom.UNKNOWN)
                .content("맛집!")
                .status(ReviewStatus.ACTIVE)
                .build();
    }
}
