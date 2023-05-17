package slcd.boost.boost.General.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.General.Entities.SubdivisionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubdivisionRepository extends JpaRepository<SubdivisionEntity, Long> {
    Optional<SubdivisionEntity> findByUuid(UUID uuid);
    List<SubdivisionEntity> findByArchived(boolean archived);
}
