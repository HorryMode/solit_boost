package slcd.boost.boost.Protocols.RegularMeetings.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Protocols.RegularMeetings.Entities.ProtocolConversationFieldEntity;

@Repository
public interface ProtocolConversationFieldRepository extends JpaRepository<ProtocolConversationFieldEntity, Long> {
}
