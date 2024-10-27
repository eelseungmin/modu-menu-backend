package modu.menu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "vote_tb")
@Entity
public class Vote extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private VoteStatus voteStatus;

    @Builder.Default
    @OneToMany(mappedBy = "vote")
    private List<Participant> participants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vote")
    private List<VoteItem> voteItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vote")
    private List<Review> reviews = new ArrayList<>();

    public void addParticipant(Participant participant) {
        participant.syncVote(this);
        participants.add(participant);
    }

    public void removeParticipant(Participant participant) {
        participant.syncVote(null);
        participants.remove(participant);
    }

    public void addVoteItem(VoteItem voteItem) {
        voteItem.syncVote(this);
        voteItems.add(voteItem);
    }

    public void removeVoteItem(VoteItem voteItem) {
        voteItem.syncVote(null);
        voteItems.remove(voteItem);
    }

    public void addReview(Review review) {
        review.syncVote(this);
        reviews.add(review);
    }

    public void removeReview(Review review) {
        review.syncVote(null);
        reviews.remove(review);
    }

    public void updateVoteStatus(VoteStatus voteStatus) {
        this.voteStatus = voteStatus;
    }
}
