package modu.menu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "place_tb")
@Entity
public class Place extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Column(length = 512)
    private String address;

    @Column(length = 30)
    private String ph;

    private String businessHours;
    private String menu;
    private Double latitude; // 위도, 소수점 다섯 번째 자리까지 사용할 경우 1m 단위까지 표현 가능
    private Double longitude; // 경도, 소수점 다섯 번째 자리까지 사용할 경우 1m 단위까지 표현 가능
    private String imageUrl;

    @Builder.Default
    @OneToMany(mappedBy = "place")
    private List<VoteItem> voteItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "place")
    private List<PlaceFood> placeFoods = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "place")
    private List<PlaceVibe> placeVibes = new ArrayList<>();

    public void addVoteItem(VoteItem voteItem) {
        voteItem.syncPlace(this);
        voteItems.add(voteItem);
    }

    public void removeVoteItem(VoteItem voteItem) {
        voteItem.syncPlace(null);
        voteItems.remove(voteItem);
    }

    public void addPlaceFood(PlaceFood placeFood) {
        placeFood.syncPlace(this);
        placeFoods.add(placeFood);
    }

    public void removePlaceFood(PlaceFood placeFood) {
        placeFood.syncPlace(null);
        placeFoods.remove(placeFood);
    }

    public void addPlaceVibe(PlaceVibe placeVibe) {
        placeVibe.syncPlace(this);
        placeVibes.add(placeVibe);
    }

    public void removePlaceVibe(PlaceVibe placeVibe) {
        placeVibe.syncPlace(null);
        placeVibes.remove(placeVibe);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(getId(), place.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
