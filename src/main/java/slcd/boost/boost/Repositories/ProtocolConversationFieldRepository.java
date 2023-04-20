package slcd.boost.boost.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Models.ProtocolConversationFieldEntity;

@Repository
public interface ProtocolConversationFieldRepository extends JpaRepository<ProtocolConversationFieldEntity, Long> {
}
