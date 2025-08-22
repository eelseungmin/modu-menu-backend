package modu.menu.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "vote_item_tb")
@Entity
public class VoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Place place;

    public void syncPlace(Place place) {
        this.place = place;
    }

    public void syncVote(Vote vote) {
        this.vote = vote;
    }
}
