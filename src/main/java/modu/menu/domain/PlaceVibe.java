package modu.menu.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "place_vibe_tb")
@Entity
public class PlaceVibe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vibe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vibe vibe;

    public void syncPlace(Place place) {
        this.place = place;
    }
}
