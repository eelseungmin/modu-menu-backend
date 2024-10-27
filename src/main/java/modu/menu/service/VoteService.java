package modu.menu.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import modu.menu.domain.Choice;
import modu.menu.repository.ChoiceRepository;
import modu.menu.core.exception.Exception400;
import modu.menu.core.exception.Exception403;
import modu.menu.core.exception.Exception404;
import modu.menu.core.response.ErrorMessage;
import modu.menu.core.util.DistanceCalculator;
import modu.menu.repository.FoodRepository;
import modu.menu.domain.Participant;
import modu.menu.domain.VoteRole;
import modu.menu.repository.ParticipantRepository;
import modu.menu.domain.Place;
import modu.menu.repository.PlaceRepository;
import modu.menu.domain.User;
import modu.menu.repository.UserRepository;
import modu.menu.repository.VibeRepository;
import modu.menu.controller.model.SaveVoteRequest;
import modu.menu.controller.model.VoteRequest;
import modu.menu.controller.model.TurnoutResponse;
import modu.menu.controller.model.VoteResponse;
import modu.menu.domain.Vote;
import modu.menu.domain.VoteStatus;
import modu.menu.repository.VoteRepository;
import modu.menu.service.model.IsVoteServiceResponse;
import modu.menu.service.model.VoteResultServiceResponse;
import modu.menu.domain.VoteItem;
import modu.menu.repository.VoteItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final ChoiceRepository choiceRepository;
    private final VoteItemRepository voteItemRepository;
    private final PlaceRepository placeRepository;
    private final VibeRepository vibeRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final HttpServletRequest request;

    // 투표 생성
    @Transactional
    public void saveVote(SaveVoteRequest saveVoteRequest) {

        List<Place> places = placeRepository.findAllById(saveVoteRequest.getPlaceIds());

        if (places.isEmpty() || places.size() != saveVoteRequest.getPlaceIds().size()) {
            throw new Exception404(ErrorMessage.NOT_EXIST_PLACE);
        }

        Vote vote = voteRepository.save(Vote.builder()
                .voteStatus(VoteStatus.ACTIVE)
                .build());
        places.forEach(place -> {
            VoteItem voteItem = voteItemRepository.save(VoteItem.builder()
                    .vote(vote)
                    .place(place)
                    .build());

            vote.addVoteItem(voteItem);
        });
        participantRepository.save(Participant.builder()
                .vote(vote)
                .user(userRepository.findById((Long) request.getAttribute("userId")).get())
                .voteRole(VoteRole.ORGANIZER)
                .build());
    }

    // 투표 초대
    @Transactional
    public void invite(Long voteId, Long userId) {

        // 투표 존재 여부를 확인한다.
        Vote vote = voteRepository.findById(voteId).orElseThrow(
                () -> new Exception404(ErrorMessage.NOT_EXIST_VOTE)
        );

        if (vote.getVoteStatus().equals(VoteStatus.END)) {
            throw new Exception404(ErrorMessage.CANT_INVITE_TO_END_VOTE);
        }

        // JWT 검증 시 존재 여부를 확인했으므로 바로 get으로 객체를 꺼낸다.
        User user = userRepository.findById(userId).get();

        Optional<Participant> participantOptional = participantRepository.findByUserId(userId);
        if (participantOptional.isEmpty()) {
            Participant participant = Participant.builder()
                    .user(user)
                    .vote(vote)
                    .voteRole(VoteRole.PARTICIPANT)
                    .build();
            vote.addParticipant(participant);
            participantRepository.save(participant);
        }
    }

    // 투표 종료
    @Transactional
    public void finishVote(Long voteId) {

        Vote vote = voteRepository.findById(voteId).orElseThrow(
                () -> new Exception404(ErrorMessage.NOT_EXIST_VOTE)
        );
        if (vote.getVoteStatus().equals(VoteStatus.END)) {
            throw new Exception400(String.valueOf(voteId), ErrorMessage.CANT_FINISH_ALREADY_END_VOTE.getValue());
        }

        Long userId = (Long) request.getAttribute("userId");
        Participant participant = participantRepository.findByUserIdAndVoteId(userId, voteId).orElseThrow(
                () -> new Exception403(ErrorMessage.NOT_ALLOWED_USER)
        );
        if (participant.getVoteRole().equals(VoteRole.PARTICIPANT)) {
            throw new Exception403(ErrorMessage.CANT_FINISH_BY_PARTICIPANT);
        }

        vote.updateVoteStatus(VoteStatus.END);
    }

    // 투표, 재투표
    @Transactional
    public void vote(Long voteId, VoteRequest voteRequest) {

        boolean isExsistsVote = voteRepository.existsById(voteId);
        if (!isExsistsVote) {
            throw new Exception404(ErrorMessage.NOT_EXIST_VOTE);
        }

        Long userId = (Long) request.getAttribute("userId");
        participantRepository.findByUserIdAndVoteId(userId, voteId).orElseThrow(
                () -> new Exception404(ErrorMessage.NOT_ALLOWED_USER)
        );

        List<VoteItem> voteItems = voteItemRepository.findByVoteId(voteId);
        Optional<Choice> choiceOptional = choiceRepository.findByUserIdAndVoteItemIds(userId, voteItems.stream()
                .map(VoteItem::getId)
                .toList());

        // case 1. 기존 투표 기록이 존재할 경우(재투표)
        if (choiceOptional.isPresent()) {
            Choice choice = choiceOptional.get();

            // case 1-1. 기존 투표 기록 삭제
            if (voteRequest.getPlaceId() == null) {
                choiceRepository.delete(choice);
                return;
            }

            // case 1-2. 기존 투표 기록 수정
            VoteItem satisfiedVoteItem = findSatisfiedVoteItem(voteRequest, voteItems);

            choice.updateVoteItem(satisfiedVoteItem);
            return;
        }

        // case 2. 기존 투표 기록이 존재하지 않을 경우(투표)
        choiceRepository.save(Choice.builder()
                .voteItem(findSatisfiedVoteItem(voteRequest, voteItems))
                .user(userRepository.findById(userId).get())
                .build());
    }

    // VoteItem 목록에서 요청 DTO 내의 placeId와 일치하는 항목을 찾는 메서드
    private VoteItem findSatisfiedVoteItem(VoteRequest voteRequest, List<VoteItem> voteItems) {
        return voteItems.stream()
                .filter(voteItem -> voteItem.getPlace().getId().equals(voteRequest.getPlaceId()))
                .findFirst()
                .orElseThrow(() -> new Exception400(String.valueOf(voteRequest.getPlaceId()), ErrorMessage.NOT_EXIST_PLACE_IN_VOTE.getValue()));
    }

    // 투표율 조회
    public TurnoutResponse getTurnout(Long voteId) {

        voteRepository.findById(voteId).orElseThrow(
                () -> new Exception404(ErrorMessage.NOT_EXIST_VOTE)
        );

        List<User> participants = participantRepository.findUserByVoteId(voteId);
        if (participants.isEmpty()) {
            return null;
        }

        List<Choice> choices = choiceRepository.findByVoteId(voteId);
        List<IsVoteServiceResponse> list = new ArrayList<>();
        int count = 0; // 투표한 사람의 인원 수
        // Stream 내부에서 final이 아닌 지역변수를 사용할 수 없으므로 아래와 같이 for-each로 대체
        for (User participant : participants) {
            boolean isVote = false;
            for (Choice choice : choices) {
                if (choice.getUser().getId().equals(participant.getId())) {
                    count++;
                    isVote = true;
                    break;
                }
            }
            list.add(IsVoteServiceResponse.builder()
                    .name(participant.getName())
                    .isVote(isVote)
                    .build());
        }

        return new TurnoutResponse(
                Math.round((double) count / participants.size() * 100) + "%",
                list
        );
    }

    // 투표 현황, 결과 조회
    public VoteResponse getVote(Long voteId, Double latitude, Double longitude) {

        // 투표 존재 여부를 확인한다. fetch join을 이용해 선택지도 함께 가져온다.
        Vote vote = voteRepository.findVoteResultById(voteId).orElseThrow(
                () -> new Exception404(ErrorMessage.NOT_EXIST_VOTE)
        );

        // 회원과 각 투표 항목에 해당하는 음식점 간의 거리를 계산한다.
        // 투표 항목 별 투표율을 계산한다.
        List<VoteItem> voteItems = vote.getVoteItems();

        Map<Long, Integer> voteCountMap = voteItems.stream()
                .collect(Collectors.toMap(VoteItem::getId, v ->
                        choiceRepository.countByVoteItemId(v.getId())));

        int voterCount = voteItems.stream()
                .mapToInt(v -> voteCountMap.getOrDefault(v.getId(), 0))
                .sum();

        List<Choice> choices = choiceRepository.findByVoteId(voteId);
        Long userId = (Long) request.getAttribute("userId");

        return new VoteResponse(voteItems.stream()
                .map(voteItem -> {
                    Place place = voteItem.getPlace();
                    double distance = DistanceCalculator.calculate(
                            place.getLatitude(),
                            place.getLongitude(),
                            latitude,
                            longitude
                    );

                    int voteCount = voteCountMap.getOrDefault(voteItem.getId(), 0);

                    return VoteResultServiceResponse.builder()
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
                            .turnout(vote.getVoteStatus() == VoteStatus.END ? Math.round(voteCount * 100.0 / voterCount) + "%" : null) // 종료된 투표일 경우에만 수치를 표시한다.
                            .isVote(
                                    choices.stream()
                                            .anyMatch(choice -> choice.getVoteItem().getId().equals(voteItem.getId()) && choice.getUser().getId().equals(userId))
                            )
                            .build();
                })
                .sorted((voteResultServiceResponse1, voteResultServiceResponse2) -> {
                    if (parseInt(voteResultServiceResponse2.getTurnout().replace("%", "")) == parseInt(voteResultServiceResponse1.getTurnout().replace("%", ""))) {
                        return voteResultServiceResponse1.getName().compareTo(voteResultServiceResponse2.getName());
                    }
                    return parseInt(voteResultServiceResponse2.getTurnout().replace("%", ""))
                            - parseInt(voteResultServiceResponse1.getTurnout().replace("%", ""));
                })
                .toList()
        );
    }
}
