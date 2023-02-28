package slcd.boost.boost.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Entities.ControlPoint;

@Repository
public interface ControlPointRepository extends JpaRepository<ControlPoint, Long> {
}
