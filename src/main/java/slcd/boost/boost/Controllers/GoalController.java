package slcd.boost.boost.Controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Entities.Goal;
import slcd.boost.boost.Services.GoalService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/Goal")
public class GoalController {

    @Autowired
    GoalService goalService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Goal newGoal(@RequestBody @Valid Goal goal){
        return goalService.saveGoal(goal);
    }

    @GetMapping("/getGoalById")
    @ResponseStatus(HttpStatus.OK)
    public Goal newGoal(@RequestParam(required = true) Long id){
        return goalService.getGoalById(id);
    }
}
