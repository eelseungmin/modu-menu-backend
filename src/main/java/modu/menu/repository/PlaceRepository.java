package modu.menu.repository;

import modu.menu.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceQuerydslRepository, PlaceJdbcRepository {
}
