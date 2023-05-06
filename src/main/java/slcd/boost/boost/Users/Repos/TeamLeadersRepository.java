package slcd.boost.boost.Users.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Users.Entities.TeamLeaderId;
import slcd.boost.boost.Users.Entities.TeamLeadersEntity;

@Repository
public interface TeamLeadersRepository extends JpaRepository<TeamLeadersEntity, TeamLeaderId>,
        JpaSpecificationExecutor<TeamLeadersEntity> {
}
