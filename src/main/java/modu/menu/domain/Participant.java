package modu.menu.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "participant_tb")
@Entity
public class Participant extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vote vote;

    @Enumerated(EnumType.STRING)
    private VoteRole voteRole;

    public void syncVote(Vote vote) {
        this.vote = vote;
    }
}
