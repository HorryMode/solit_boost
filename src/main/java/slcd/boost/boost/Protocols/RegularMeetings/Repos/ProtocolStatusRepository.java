package slcd.boost.boost.Protocols.RegularMeetings.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EProtocolStatus;
import slcd.boost.boost.Protocols.Entities.ProtocolStatusEntity;

@Repository
public interface ProtocolStatusRepository extends JpaRepository<ProtocolStatusEntity, Long> {
    ProtocolStatusEntity findByName(EProtocolStatus name);
}
