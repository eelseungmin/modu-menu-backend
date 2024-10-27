package modu.menu.repository;

import modu.menu.IntegrationTestSupporter;
import modu.menu.domain.Participant;
import modu.menu.domain.VoteRole;
import modu.menu.domain.Gender;
import modu.menu.domain.User;
import modu.menu.domain.UserStatus;
import modu.menu.domain.Vote;
import modu.menu.domain.VoteStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipantRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @DisplayName("userId를 통해 회원이 투표에 초대받은 사람인지 확인한다.")
    @Test
    void findByUserId() {
        // given
        User user1 = createUser("gildong123@naver.com");
        User user2 = createUser("seungmin123@naver.com");
        Vote vote1 = createVote(VoteStatus.ACTIVE);
        Participant participant = createParticipant(user1, vote1, VoteRole.PARTICIPANT);
        userRepository.saveAll(List.of(user1, user2));
        voteRepository.save(vote1);
        participantRepository.save(participant);

        Long userId = user1.getId();

        // when
        Optional<Participant> result = participantRepository.findByUserId(userId);

        // then
        assertThat(result)
                .isNotEmpty()
                .get()
                .hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result.get().getUser().getId()).isEqualTo(1L);
    }

    @DisplayName("초대 받지 않은 회원의 userId를 이용해 초대 받은 사람인지 확인한다.")
    @Test
    void findByUserIdReturnEmptyParticipant() {
        // given
        User user1 = createUser("gildong123@naver.com");
        User user2 = createUser("seungmin123@naver.com");
        Vote vote1 = createVote(VoteStatus.ACTIVE);
        Participant participant = createParticipant(user1, vote1, VoteRole.PARTICIPANT);
        userRepository.saveAll(List.of(user1, user2));
        voteRepository.save(vote1);
        participantRepository.save(participant);

        Long userId = user2.getId();

        // when
        Optional<Participant> result = participantRepository.findByUserId(userId);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("userId, voteId를 통해 회원이 투표에 초대 받은 사람인지 확인한다.")
    @Test
    void findByUserIdAndVoteId() {
        // given
        User user1 = createUser("gildong123@naver.com");
        User user2 = createUser("seungmin123@naver.com");
        Vote vote1 = createVote(VoteStatus.ACTIVE);
        Participant participant = createParticipant(user1, vote1, VoteRole.PARTICIPANT);
        userRepository.saveAll(List.of(user1, user2));
        voteRepository.save(vote1);
        participantRepository.save(participant);

        // when
        Optional<Participant> result = participantRepository.findByUserIdAndVoteId(user1.getId(), vote1.getId());

        // then
        assertThat(result)
                .isNotEmpty()
                .get()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @DisplayName("초대 받지 않은 회원의 userId와 voteId를 통해 회원이 투표에 초대 받은 사람인지 확인한다.")
    @Test
    void findByUserIdAndVoteIdReturnEmptyParticipant() {
        // given
        User user1 = createUser("gildong123@naver.com");
        User user2 = createUser("seungmin123@naver.com");
        Vote vote1 = createVote(VoteStatus.ACTIVE);
        Participant participant = createParticipant(user1, vote1, VoteRole.PARTICIPANT);
        userRepository.saveAll(List.of(user1, user2));
        voteRepository.save(vote1);
        participantRepository.save(participant);

        // when
        Optional<Participant> result = participantRepository.findByUserIdAndVoteId(user2.getId(), vote1.getId());

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("voteId를 통해 투표에 초대받은 회원 목록을 조회한다.")
    @Test
    void findByVoteId() {
        // given
        User user1 = createUser("gildong123@naver.com");
        User user2 = createUser("seungmin123@naver.com");
        Vote vote1 = createVote(VoteStatus.ACTIVE);
        Participant participant = createParticipant(user1, vote1, VoteRole.PARTICIPANT);
        userRepository.saveAll(List.of(user1, user2));
        voteRepository.save(vote1);
        participantRepository.save(participant);

        // when
        List<User> result = participantRepository.findUserByVoteId(vote1.getId());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result)
                .extracting("id")
                .containsExactlyInAnyOrder(1L);
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

    private Vote createVote(VoteStatus voteStatus) {
        return Vote.builder()
                .voteStatus(voteStatus)
                .voteItems(new ArrayList<>())
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
