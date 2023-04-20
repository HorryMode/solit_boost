package slcd.boost.boost.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Models.Enum.EProtocolType;
import slcd.boost.boost.Models.ProtocolEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProtocolRepository extends JpaRepository<ProtocolEntity, UUID> {
    Optional<ProtocolEntity> findByUuidAndType(UUID uuid, EProtocolType type);
    ProtocolEntity findByUuid(UUID uuid);
}
