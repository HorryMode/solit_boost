package slcd.boost.boost.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Entities.TypicalGoal;

@Repository
public interface TypicalGoalRepository extends JpaRepository<TypicalGoal, Long> {
}
