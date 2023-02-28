package slcd.boost.boost.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Entities.Goal;
import slcd.boost.boost.Models.ResourseNotFoundException;
import slcd.boost.boost.Repositories.GoalRepository;

import java.util.Optional;

@Service
public class GoalService {

    @Autowired
    GoalRepository goalRepository;

    public Goal saveGoal(Goal goal){
        return goalRepository.save(goal);
    }

    public Goal getGoalById(Long id){
        return goalRepository.findById(id).orElseThrow(
                ()-> new ResourseNotFoundException("Goal with id " + id + " not found")
        );
    }
}
