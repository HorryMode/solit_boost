package slcd.boost.boost.Protocols.RegularMeetings.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Protocols.Entities.AttachmentEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProtocolConversationAttachmentRepository
        extends JpaRepository<AttachmentEntity, Long> {
    boolean existsByField_Uuid(UUID uuid);
    Optional<AttachmentEntity> findByUuid(UUID uuid);
}
