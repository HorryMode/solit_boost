package slcd.boost.boost.Protocols.RegularMeetings.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Protocols.Entities.ProtocolConversationAttachmentEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProtocolConversationAttachmentRepository
        extends JpaRepository<ProtocolConversationAttachmentEntity, Long> {
    boolean existsByField_Uuid(UUID uuid);
    Optional<ProtocolConversationAttachmentEntity> findByUuid(UUID uuid);
}
