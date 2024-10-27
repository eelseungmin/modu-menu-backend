package modu.menu.repository;

import modu.menu.domain.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {

    @Query("""
            select vi
            from VoteItem vi
            where vi.vote.id = :voteId
            """)
    List<VoteItem> findByVoteId(@Param("voteId") Long voteId);
}
