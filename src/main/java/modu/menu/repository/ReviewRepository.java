package modu.menu.repository;

import modu.menu.domain.Review;
import modu.menu.domain.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
            select count(r)
            from Review r
            join User u on u.id = r.user.id
            join Vote v on v.id = r.vote.id
            where u.id = :userId and v.id = :voteId and v.voteStatus = :voteStatus
            """)
    int countByUserIdAndVoteIdAndVoteStatus(@Param("userId") Long userId, @Param("voteId") Long voteId, @Param("voteStatus") VoteStatus voteStatus);
}
