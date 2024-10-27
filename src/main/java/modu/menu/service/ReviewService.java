package modu.menu.service;

import lombok.RequiredArgsConstructor;
import modu.menu.repository.ChoiceRepository;
import modu.menu.domain.Place;
import modu.menu.repository.PlaceRepository;
import modu.menu.controller.model.CreateReviewRequest;
import modu.menu.controller.model.VibeRequest;
import modu.menu.controller.model.CheckReviewNecessityResponse;
import modu.menu.domain.HasRoom;
import modu.menu.domain.Review;
import modu.menu.domain.ReviewStatus;
import modu.menu.repository.ReviewRepository;
import modu.menu.service.model.IncompletePlaceServiceResponse;
import modu.menu.domain.ReviewVibe;
import modu.menu.repository.ReviewVibeRepository;
import modu.menu.domain.User;
import modu.menu.repository.UserRepository;
import modu.menu.domain.VibeType;
import modu.menu.repository.VibeRepository;
import modu.menu.domain.Vote;
import modu.menu.domain.VoteStatus;
import modu.menu.repository.VoteRepository;
import modu.menu.domain.VoteItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static modu.menu.core.util.EnumConverter.stringToEnum;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewVibeRepository reviewVibeRepository;
    private final VibeRepository vibeRepository;
    private final VoteRepository voteRepository;
    private final ChoiceRepository choiceRepository;

    // 리뷰가 필요한 음식점 목록 조회
    public CheckReviewNecessityResponse checkReviewNecessity(Long userId) {

        // 토큰 검증 시 해당 회원의 존재 및 탈퇴 여부를 체크했으므로 여기선 생략한다.
        User user = userRepository.findById(userId).get();

        // 종료된 투표들 중 회원이 참가했던 투표들을 찾는다.
        List<Vote> votes = voteRepository.findByUserIdAndVoteStatus(userId, VoteStatus.END);

        // 투표 목록이 없다면 null을 반환한다.
        if (votes.isEmpty()) {
            return null;
        }

        // 회원이 작성한 리뷰가 없는 투표만 골라낸다.
        List<Vote> votesDoNotHaveUserReview = votes.stream()
                .filter(vote -> vote.getReviews().stream()
                        .anyMatch(review -> !review.getUser().getId().equals(userId)))
                .toList();

        // 참가했던 투표에 리뷰를 전부 작성했으므로 null을 반환한다.
        if (votesDoNotHaveUserReview.isEmpty()) {
            return null;
        }

        // 회원이 작성한 리뷰가 없는 각 투표의 선택지에 대해 득표율을 계산한다. 이때 득표율이 동일한 경우도 체크한다.
        for (Vote vote : votesDoNotHaveUserReview) {
            List<VoteItem> voteItems = vote.getVoteItems();

            Map<Long, Integer> voteCountMap = voteItems.stream()
                    .collect(Collectors.toMap(VoteItem::getId, v ->
                            choiceRepository.countByVoteItemId(v.getId())));

            int voterCount = voteItems.stream()
                    .mapToInt(v -> voteCountMap.getOrDefault(v.getId(), 0))
                    .sum();

            Set<Long> distinctTurnouts = new HashSet<>();
            List<Place> duplicatePlaces = new ArrayList<>();
            List<Place> calculatedPlaces = voteItems.stream()
                    .map(voteItem -> {
                        int voteCount = voteCountMap.getOrDefault(voteItem.getId(), 0);
                        Long turnout = Math.round(voteCount * 100.0 / voterCount);
                        distinctTurnouts.add(turnout);

                        return Map.entry(voteItem.getPlace(), turnout);
                    })
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .peek(entry -> { // 동일 득표율 항목 여부 체크
                        if (distinctTurnouts.contains(entry.getValue())) {
                            duplicatePlaces.add(entry.getKey());
                        }
                    })
                    .map(Map.Entry::getKey)
                    .toList();

            // case 1. 득표율이 동일한 항목들이 존재한다면
            if (hasDuplicateTurnout(duplicatePlaces, calculatedPlaces)) {

                // 동일한 항목을 모두 응답 리스트에 포함시키고 isSameTurnout을 true로 설정한다.
                return new CheckReviewNecessityResponse(
                        true,
                        duplicatePlaces.stream()
                                .map(place -> IncompletePlaceServiceResponse.builder()
                                        .id(place.getId())
                                        .name(place.getName())
                                        .foods(place.getPlaceFoods().stream()
                                                .map(placeFood -> placeFood.getFood().getType())
                                                .toList())
                                        .address(place.getAddress())
                                        .img(place.getImageUrl())
                                        .build())
                                .toList()
                );
            }

            // case 2. 득표율이 동일한 항목들이 없다면
            // case 2-2. 득표율이 가장 높은 항목만 응답 리스트에 포함시키고 isSameTurnout을 false로 설정한다.
            return new CheckReviewNecessityResponse(
                    false,
                    List.of(calculatedPlaces.stream().findFirst().map(place -> IncompletePlaceServiceResponse.builder()
                            .id(place.getId())
                            .name(place.getName())
                            .foods(place.getPlaceFoods().stream()
                                    .map(placeFood -> placeFood.getFood().getType())
                                    .toList())
                            .address(place.getAddress())
                            .img(place.getImageUrl())
                            .build()).orElseThrow())
            );
        }

        return null;
    }

    // 동일 득표율 항목 여부 체크
    // 득표율이 동일한 항목이 있더라도 그 득표율이 선택지 중에서 가장 높지 않으면 의미가 없다.
    private boolean hasDuplicateTurnout(List<Place> duplicatePlaces, List<Place> calculatedPlaces) {
        return !duplicatePlaces.isEmpty()
                && (duplicatePlaces.get(0).getId().equals(calculatedPlaces.get(0).getId()) || duplicatePlaces.get(0).getId().equals(calculatedPlaces.get(1).getId()));
    }

    // 리뷰 등록
    @Transactional
    public void createReview(Long userId, Long placeId, CreateReviewRequest createReviewRequest) {

        Review review = reviewRepository.save(Review.builder()
                .user(userRepository.findById(userId).get())
                .place(placeRepository.findById(placeId).get())
                .rating(createReviewRequest.getRating())
                .participants(createReviewRequest.getParticipants())
                .hasRoom(stringToEnum(HasRoom.class, createReviewRequest.getHasRoom()))
                .content(createReviewRequest.getContent())
                .status(ReviewStatus.ACTIVE)
                .build());

        if (createReviewRequest.getVibes() != null
                && !createReviewRequest.getVibes().isEmpty()) {
            for (VibeRequest vr : createReviewRequest.getVibes()) {
                reviewVibeRepository.save(
                        ReviewVibe.builder()
                                .review(review)
                                .vibe(vibeRepository.findByVibeType(stringToEnum(VibeType.class, vr.getType())).get())
                                .build()
                );
            }
        }
    }
}
