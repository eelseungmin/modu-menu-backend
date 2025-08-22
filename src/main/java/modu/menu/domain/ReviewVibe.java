package modu.menu.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "review_vibe_tb")
@Entity
public class ReviewVibe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vibe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vibe vibe;
}
