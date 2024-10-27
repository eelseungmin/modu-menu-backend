package modu.menu.service;

import jakarta.servlet.http.HttpServletRequest;
import modu.menu.IntegrationTestSupporter;
import modu.menu.domain.Choice;
import modu.menu.repository.ChoiceRepository;
import modu.menu.core.exception.Exception400;
import modu.menu.core.exception.Exception403;
import modu.menu.core.exception.Exception404;
import modu.menu.core.response.ErrorMessage;
import modu.menu.domain.Food;
import modu.menu.domain.FoodType;
import modu.menu.repository.FoodRepository;
import modu.menu.domain.Participant;
import modu.menu.domain.VoteRole;
import modu.menu.repository.ParticipantRepository;
import modu.menu.domain.Place;
import modu.menu.repository.PlaceRepository;
import modu.menu.domain.PlaceFood;
import modu.menu.repository.PlaceFoodRepository;
import modu.menu.domain.PlaceVibe;
import modu.menu.repository.PlaceVibeRepository;
import modu.menu.domain.Gender;
import modu.menu.domain.User;
import modu.menu.domain.UserStatus;
import modu.menu.repository.UserRepository;
import modu.menu.domain.Vibe;
import modu.menu.domain.VibeType;
import modu.menu.repository.VibeRepository;
import modu.menu.controller.model.SaveVoteRequest;
import modu.menu.controller.model.TurnoutResponse;
import modu.menu.controller.model.VoteResponse;
import modu.menu.domain.Vote;
import modu.menu.domain.VoteStatus;
import modu.menu.repository.VoteRepository;
import modu.menu.domain.VoteItem;
import modu.menu.repository.VoteItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class VoteServiceTest extends IntegrationTestSupporter {

    @Autowired
    private VoteService voteService;
    @Autowired
    private UserRepository userRepository;
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
    private VoteRepository voteRepository;
    @Autowired
    private VoteItemRepository voteItemRepository;
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private HttpServletRequest request;

    @DisplayName("투표를 생성한다.")
    @Test
    void saveVote() {
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

        SaveVoteRequest saveVoteRequest = SaveVoteRequest.builder()
                .placeIds(List.of(1L, 2L, 3L))
                .build();

        request.setAttribute("userId", user1.getId());

        // when
        voteService.saveVote(saveVoteRequest);


        // then
        assertThat(voteRepository.findAll()).isNotEmpty();
        assertThat(participantRepository.findByUserId(user1.getId()).get().getVoteRole()).isEqualTo(VoteRole.ORGANIZER);
    }

    @DisplayName("투표를 생성할 때 ID 목록에 있는 음식점들이 존재하지 않으면 실패한다.")
    @Test
    void saveVoteWithNotExistIds() {
        // given
        SaveVoteRequest saveVoteRequest = SaveVoteRequest.builder()
                .placeIds(List.of(1L, 2L, 3L))
                .build();

        // when


        // then
        assertThatThrownBy(() -> voteService.saveVote(saveVoteRequest))
                .isInstanceOf(Exception404.class)
                .hasMessage("해당 ID와 일치하는 음식점이 존재하지 않습니다.");
    }

    @DisplayName("투표를 생성할 때 ID 목록 중 존재하지 않는 음식점의 ID가 있다면 실패한다.")
    @Test
    void saveVoteWithNotExistId() {
        // given
        Place place1 = createPlace("타코벨");
        Place place2 = createPlace("이자카야모리");
        Vibe vibe1 = createVibe(VibeType.NOISY);
        Vibe vibe2 = createVibe(VibeType.QUIET);
        Vibe vibe3 = createVibe(VibeType.GOOD_SERVICE);
        PlaceVibe placeVibe1 = createPlaceVibe(place1, vibe1);
        place1.addPlaceVibe(placeVibe1);
        PlaceVibe placeVibe2 = createPlaceVibe(place2, vibe2);
        place2.addPlaceVibe(placeVibe2);
        placeRepository.saveAll(List.of(place1, place2));
        vibeRepository.saveAll(List.of(vibe1, vibe2, vibe3));
        placeVibeRepository.saveAll(List.of(placeVibe1, placeVibe2));
        Food food1 = createFood(FoodType.LATIN);
        Food food2 = createFood(FoodType.MEAT);
        PlaceFood placeFood1 = createPlaceFood(place1, food1);
        place1.addPlaceFood(placeFood1);
        PlaceFood placeFood2 = createPlaceFood(place2, food1);
        place2.addPlaceFood(placeFood2);
        foodRepository.saveAll(List.of(food1, food2));
        placeFoodRepository.saveAll(List.of(placeFood1, placeFood2));

        SaveVoteRequest saveVoteRequest = SaveVoteRequest.builder()
                .placeIds(List.of(1L, 2L, 3L))
                .build();

        // when


        // then
        assertThatThrownBy(() -> voteService.saveVote(saveVoteRequest))
                .isInstanceOf(Exception404.class)
                .hasMessage("해당 ID와 일치하는 음식점이 존재하지 않습니다.");
    }

    @DisplayName("회원을 투표에 초대한다.")
    @Test
    void invite() {
        // given
        User user1 = createUser("gildong123@naver.com");
        Vote vote1 = createVote(VoteStatus.ACTIVE);
        userRepository.save(user1);
        voteRepository.save(vote1);
        Long userId = user1.getId();
        Long voteId = vote1.getId();

        // when
        voteService.invite(voteId, userId);

        // then
        assertThat(participantRepository.findByUserIdAndVoteId(userId, voteId).get()).isNotNull();
    }

    @DisplayName("존재하지 않는 투표에 회원을 초대할 수 없다.")
    @Test
    void inviteWithNotExistVote() {
        // given
        User user1 = createUser("gildong123@naver.com");
        userRepository.save(user1);
        Long userId = user1.getId();
        Long voteId = 1L;

        // when
        assertThatThrownBy(() -> voteService.invite(voteId, userId))
                .isInstanceOf(Exception404.class)
                .hasMessage(ErrorMessage.NOT_EXIST_VOTE.getValue());
    }

    @DisplayName("종료된 투표에 회원을 초대할 수 없다.")
    @Test
    void inviteWithEndVote() {
        // given
        User user1 = createUser("gildong123@naver.com");
        Vote vote1 = createVote(VoteStatus.END);
        userRepository.save(user1);
        voteRepository.save(vote1);
        Long userId = user1.getId();
        Long voteId = vote1.getId();

        // when
        assertThatThrownBy(() -> voteService.invite(voteId, userId))
                .isInstanceOf(Exception404.class)
                .hasMessage(ErrorMessage.CANT_INVITE_TO_END_VOTE.getValue());
    }

    @DisplayName("투표를 종료한다.")
    @Test
    void finishVote() {
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

        Vote vote = createVote(VoteStatus.ACTIVE);
        VoteItem voteItem1 = createVoteItem(vote, place1);
        VoteItem voteItem2 = createVoteItem(vote, place2);
        VoteItem voteItem3 = createVoteItem(vote, place3);
        vote.addVoteItem(voteItem1);
        vote.addVoteItem(voteItem2);
        vote.addVoteItem(voteItem3);
        Choice choice1 = createChoice(voteItem1, user1);
        Choice choice2 = createChoice(voteItem2, user2);
        Choice choice3 = createChoice(voteItem3, user3);
        Participant participant = createParticipant(user1, vote, VoteRole.ORGANIZER);
        voteRepository.save(vote);
        voteItemRepository.saveAll(List.of(voteItem1, voteItem2, voteItem3));
        choiceRepository.saveAll(List.of(choice1, choice2, choice3));
        participantRepository.save(participant);

        Long voteId = 1L;
        request.setAttribute("userId", 1L);

        // when
        voteService.finishVote(voteId);

        // then
        assertThat(voteRepository.findById(voteId).get().getVoteStatus()).isEqualTo(VoteStatus.END);
    }

    @DisplayName("존재하지 않는 투표를 종료할 수 없다.")
    @Test
    void finishNotExistVote() {
        // given
        Long voteId = 1L;

        // when

        // then
        assertThatThrownBy(() -> voteService.finishVote(voteId))
                .isInstanceOf(Exception404.class)
                .hasMessage(ErrorMessage.NOT_EXIST_VOTE.getValue());
    }

    @DisplayName("이미 종료된 투표를 종료할 수 없다.")
    @Test
    void test() {
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
        Choice choice1 = createChoice(voteItem1, user1);
        Choice choice2 = createChoice(voteItem2, user2);
        Choice choice3 = createChoice(voteItem3, user3);
        Participant participant = createParticipant(user1, vote, VoteRole.ORGANIZER);
        voteRepository.save(vote);
        voteItemRepository.saveAll(List.of(voteItem1, voteItem2, voteItem3));
        choiceRepository.saveAll(List.of(choice1, choice2, choice3));
        participantRepository.save(participant);

        Long voteId = 1L;
        request.setAttribute("userId", 1L);

        // when

        // then
        assertThatThrownBy(() -> voteService.finishVote(voteId))
                .isInstanceOf(Exception400.class)
                .hasMessage(ErrorMessage.CANT_FINISH_ALREADY_END_VOTE.getValue());
    }

    @DisplayName("초대받지 않은 회원이 투표를 종료할 수 없다.")
    @Test
    void finishVoteByNotAllowedUser() {
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

        Vote vote = createVote(VoteStatus.ACTIVE);
        VoteItem voteItem1 = createVoteItem(vote, place1);
        VoteItem voteItem2 = createVoteItem(vote, place2);
        VoteItem voteItem3 = createVoteItem(vote, place3);
        vote.addVoteItem(voteItem1);
        vote.addVoteItem(voteItem2);
        vote.addVoteItem(voteItem3);
        Choice choice1 = createChoice(voteItem1, user1);
        Choice choice2 = createChoice(voteItem2, user2);
        Choice choice3 = createChoice(voteItem3, user3);
        voteRepository.save(vote);
        voteItemRepository.saveAll(List.of(voteItem1, voteItem2, voteItem3));
        choiceRepository.saveAll(List.of(choice1, choice2, choice3));

        Long voteId = 1L;
        request.setAttribute("userId", 1L);

        // when

        // then
        assertThatThrownBy(() -> voteService.finishVote(voteId))
                .isInstanceOf(Exception403.class)
                .hasMessage(ErrorMessage.NOT_ALLOWED_USER.getValue());
    }

    @DisplayName("초대를 받았더라도 주최자가 아닌 회원이 투표를 종료할 수 없다.")
    @Test
    void finishVoteByParticipant() {
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

        Vote vote = createVote(VoteStatus.ACTIVE);
        VoteItem voteItem1 = createVoteItem(vote, place1);
        VoteItem voteItem2 = createVoteItem(vote, place2);
        VoteItem voteItem3 = createVoteItem(vote, place3);
        vote.addVoteItem(voteItem1);
        vote.addVoteItem(voteItem2);
        vote.addVoteItem(voteItem3);
        Choice choice1 = createChoice(voteItem1, user1);
        Choice choice2 = createChoice(voteItem2, user2);
        Choice choice3 = createChoice(voteItem3, user3);
        Participant participant = createParticipant(user1, vote, VoteRole.PARTICIPANT);
        voteRepository.save(vote);
        voteItemRepository.saveAll(List.of(voteItem1, voteItem2, voteItem3));
        choiceRepository.saveAll(List.of(choice1, choice2, choice3));
        participantRepository.save(participant);

        Long voteId = 1L;
        request.setAttribute("userId", 1L);

        // when

        // then
        assertThatThrownBy(() -> voteService.finishVote(voteId))
                .isInstanceOf(Exception403.class)
                .hasMessage(ErrorMessage.CANT_FINISH_BY_PARTICIPANT.getValue());
    }

    @DisplayName("투표율을 조회한다.")
    @Test
    void getTurnout() {
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

        Vote vote = createVote(VoteStatus.ACTIVE);
        VoteItem voteItem1 = createVoteItem(vote, place1);
        VoteItem voteItem2 = createVoteItem(vote, place2);
        VoteItem voteItem3 = createVoteItem(vote, place3);
        vote.addVoteItem(voteItem1);
        vote.addVoteItem(voteItem2);
        vote.addVoteItem(voteItem3);
        Choice choice1 = createChoice(voteItem1, user1);
        Choice choice3 = createChoice(voteItem3, user3);
        Participant participant = createParticipant(user1, vote, VoteRole.ORGANIZER);
        Participant participant2 = createParticipant(user2, vote, VoteRole.PARTICIPANT);
        Participant participant3 = createParticipant(user3, vote, VoteRole.PARTICIPANT);
        voteRepository.save(vote);
        voteItemRepository.saveAll(List.of(voteItem1, voteItem2, voteItem3));
        choiceRepository.saveAll(List.of(choice1, choice3));
        participantRepository.saveAll(List.of(participant, participant2, participant3));

        Long voteId = 1L;

        // when
        TurnoutResponse turnout = voteService.getTurnout(voteId);

        // then
        assertThat(turnout).isNotNull();
        assertThat(turnout.getTurnout()).isEqualTo("67%");
        assertThat(turnout.getParticipants())
                .extracting("name", "isVote")
                .containsExactlyInAnyOrder(
                        tuple("name", true),
                        tuple("name", false),
                        tuple("name", true)
                );
    }

    @DisplayName("투표율을 조회할 때 존재하지 않는 voteId로 요청하면 실패한다.")
    @Test
    void getTurnoutWithNotExistVoteId() {
        // given
        Long voteId = 1L;

        // when

        // then
        assertThatThrownBy(() -> voteService.getTurnout(voteId))
                .isInstanceOf(Exception404.class)
                .hasMessage(ErrorMessage.NOT_EXIST_VOTE.getValue());
    }

    @DisplayName("투표율을 조회할 때 투표에 초대받은 이가 없으면 null을 반환한다.")
    @Test
    void getTurnoutWithNotExistParticipant() {
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

        Vote vote = createVote(VoteStatus.ACTIVE);
        VoteItem voteItem1 = createVoteItem(vote, place1);
        VoteItem voteItem2 = createVoteItem(vote, place2);
        VoteItem voteItem3 = createVoteItem(vote, place3);
        vote.addVoteItem(voteItem1);
        vote.addVoteItem(voteItem2);
        vote.addVoteItem(voteItem3);
        Choice choice1 = createChoice(voteItem1, user1);
        Choice choice3 = createChoice(voteItem3, user3);
        voteRepository.save(vote);
        voteItemRepository.saveAll(List.of(voteItem1, voteItem2, voteItem3));
        choiceRepository.saveAll(List.of(choice1, choice3));

        Long voteId = 1L;

        // when
        TurnoutResponse turnout = voteService.getTurnout(voteId);

        // then
        assertThat(turnout).isNull();
    }

    @DisplayName("투표 ID를 통해 투표 및 연관 데이터를 조회한다.")
    @Test
    void getVoteResult() {
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
        Choice choice1 = createChoice(voteItem1, user1);
        Choice choice2 = createChoice(voteItem2, user2);
        Choice choice3 = createChoice(voteItem3, user3);
        voteRepository.save(vote);
        voteItemRepository.saveAll(List.of(voteItem1, voteItem2, voteItem3));
        choiceRepository.saveAll(List.of(choice1, choice2, choice3));

        Long voteId = 1L;
        Double latitude = 37.655038011447;
        Double longitude = 127.06694995614;
        request.setAttribute("userId", user1.getId());

        // when
        VoteResponse voteResult = voteService.getVote(voteId, latitude, longitude);

        // then
        assertThat(voteResult.getVote()).isNotNull();
        assertThat(voteResult.getVote())
                .extracting("name", "foods", "address", "distance", "img", "turnout", "isVote")
                .containsExactlyInAnyOrder(
                        tuple("타코벨", List.of(FoodType.LATIN), "address", "2.5km", "image", "33%", true),
                        tuple("이자카야모리", List.of(FoodType.LATIN), "address", "2.5km", "image", "33%", false),
                        tuple("서가앤쿡 노원역점", List.of(FoodType.MEAT), "address", "2.5km", "image", "33%", false)
                );
        assertThat(
                voteResult.getVote().stream()
                        .flatMap(v -> v.getVibes().stream())
                        .toList()
        )
                .extracting("title")
                .containsExactlyInAnyOrder(
                        VibeType.NOISY.getTitle(),
                        VibeType.QUIET.getTitle(),
                        VibeType.GOOD_SERVICE.getTitle()
                );
    }

    @DisplayName("DB에 존재하는 투표의 ID로 조회를 요청해야 한다.")
    @Test
    void getVoteResultWithNotExistVoteId() {
        // given
        Long voteId = 2L;
        Double latitude = 37.655038011447;
        Double longitude = 127.06694995614;

        // when & then
        assertThatThrownBy(() -> voteService.getVote(voteId, latitude, longitude))
                .isInstanceOf(Exception404.class)
                .hasMessage(ErrorMessage.NOT_EXIST_VOTE.getValue());
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

    private Participant createParticipant(User user, Vote vote, VoteRole voteRole) {
        return Participant.builder()
                .user(user)
                .vote(vote)
                .voteRole(voteRole)
                .build();
    }
}
