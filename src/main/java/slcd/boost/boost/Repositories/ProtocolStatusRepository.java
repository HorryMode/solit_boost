package slcd.boost.boost.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Models.Enum.EProtocolStatus;
import slcd.boost.boost.Models.ProtocolStatusEntity;

@Repository
public interface ProtocolStatusRepository extends JpaRepository<ProtocolStatusEntity, Long> {
    ProtocolStatusEntity findByName(EProtocolStatus name);
}
