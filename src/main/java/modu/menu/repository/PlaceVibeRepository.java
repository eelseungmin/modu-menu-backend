package modu.menu.repository;

import modu.menu.domain.PlaceVibe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceVibeRepository extends JpaRepository<PlaceVibe, Long>, PlaceVibeJdbcRepository {
}
