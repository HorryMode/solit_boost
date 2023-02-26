package slcd.boost.boost.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Entities.TypicalGoal;
import slcd.boost.boost.Repositories.TypicalGoalRepository;

@Service
public class TypicalGoalService {

    @Autowired
    TypicalGoalRepository typicalGoalRepository;

    public TypicalGoal saveTypicalGoal(TypicalGoal typicalGoal){
        return typicalGoalRepository.save(typicalGoal);
    }
}
