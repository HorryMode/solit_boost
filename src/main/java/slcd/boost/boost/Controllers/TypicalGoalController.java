package slcd.boost.boost.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Entities.TypicalGoal;
import slcd.boost.boost.Services.TypicalGoalService;

@RestController
@RequestMapping("/api/v1/TypicalGoal")
public class TypicalGoalController {

    @Autowired
    TypicalGoalService typicalGoalService;

    @GetMapping("/getAllTypicalGoals")
    public ResponseEntity<Long> getAllTypicalGoal(){
        return new ResponseEntity<Long>(Long.valueOf(1), HttpStatus.CREATED);
    };

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TypicalGoal newTypicalGoal(){
        return typicalGoalService.saveTypicalGoal(new TypicalGoal());
    }
}
