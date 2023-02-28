package slcd.boost.boost.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Entities.ControlPoint;
import slcd.boost.boost.Repositories.ControlPointRepository;

@Service
public class ControlPointService {

    @Autowired
    ControlPointRepository controlPointRepository;

    public ControlPoint saveControlPoint(ControlPoint controlPoint){
        return controlPointRepository.save(controlPoint);
    }
}
