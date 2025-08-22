package modu.menu.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "review_tb")
@Entity
public class Review extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user; // 리뷰어

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Place place; // 리뷰 대상

    private Integer rating; // 평점
    private Integer participants; // 총 인원 수

    @Enumerated(EnumType.STRING)
    private HasRoom hasRoom; // 룸 여부

    @Column(length = 512)
    private String content;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    public void syncVote(Vote vote) {
        this.vote = vote;
    }
}
