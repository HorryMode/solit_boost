package slcd.boost.boost.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Models.TeamLeaderId;
import slcd.boost.boost.Models.TeamLeadersEntity;

@Repository
public interface TeamLeadersRepository extends JpaRepository<TeamLeadersEntity, TeamLeaderId>{

}
