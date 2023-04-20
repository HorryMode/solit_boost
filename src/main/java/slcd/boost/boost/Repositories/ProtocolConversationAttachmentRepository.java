package slcd.boost.boost.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Models.ProtocolConversationAttachmentEntity;
import slcd.boost.boost.Payloads.General.RegularMeeting.ProtocolConversationAttachment;

@Repository
public interface ProtocolConversationAttachmentRepository
        extends JpaRepository<ProtocolConversationAttachmentEntity, Long> {
}
