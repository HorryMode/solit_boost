package slcd.boost.boost.Controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Entities.ControlPoint;
import slcd.boost.boost.Services.ControlPointService;

@RestController
@RequestMapping("/api/v1/ControlPoint")
public class ControlPointController {

    @Autowired
    ControlPointService controlPointService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ControlPoint newControlPoint(@RequestBody @Valid ControlPoint controlPoint){
        return controlPointService.saveControlPoint(controlPoint);
    }
}
