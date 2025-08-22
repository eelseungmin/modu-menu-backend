package modu.menu.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "place_food_tb")
@Entity
public class PlaceFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Food food;

    public void syncPlace(Place place) {
        this.place = place;
    }
}
