package modu.menu.repository;

import modu.menu.domain.AgreementHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgreementHistoryRepository extends JpaRepository<AgreementHistory, Long> {
}
