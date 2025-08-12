package modu.menu.repository;

import modu.menu.domain.PlaceVibe;

import java.util.List;

public interface PlaceVibeJdbcRepository {

    void insertDummyData(List<PlaceVibe> placeVibes);
}
