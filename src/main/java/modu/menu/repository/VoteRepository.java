package modu.menu.repository;

import modu.menu.domain.Vote;
import modu.menu.domain.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("""
            select v
            from Vote v
            join fetch v.voteItems
            where v.id = :voteId
            """)
    Optional<Vote> findVoteResultById(@Param("voteId") Long voteId);

    @Query("""
            select v
            from Vote v
            join VoteItem vi on vi.vote.id = v.id
            join Choice c on c.voteItem.id = vi.id
            where c.user.id = :userId and v.voteStatus = :voteStatus
            """)
    List<Vote> findByUserIdAndVoteStatus(@Param("userId") Long userId, @Param("voteStatus") VoteStatus voteStatus);
}
