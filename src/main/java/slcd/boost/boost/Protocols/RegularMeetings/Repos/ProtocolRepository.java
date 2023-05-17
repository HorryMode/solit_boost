package slcd.boost.boost.Protocols.RegularMeetings.Repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EProtocolType;
import slcd.boost.boost.Protocols.Entities.ProtocolEntity;
import slcd.boost.boost.Users.Entities.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProtocolRepository extends JpaRepository<ProtocolEntity, UUID> {
    ProtocolEntity findByFields_Attachments_Uuid(UUID uuid);
    ProtocolEntity findByUuidAndFields_IdAndFields_Attachments_Uuid(UUID uuid, Long id, UUID uuid1);
    Optional<ProtocolEntity> findByUuidAndType(UUID uuid, EProtocolType type);
    ProtocolEntity findByUuid(UUID uuid);
    Page<ProtocolEntity> findByOwner(UserEntity user, Pageable pageable);
}
