package slcd.boost.boost.General.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import slcd.boost.boost.General.Entities.PostEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByArchived(boolean archived);
    boolean existsByUuidAndArchived(UUID uuid, boolean archived);
    boolean existsByUuid(UUID uuid);
    Optional<PostEntity> findByUuid(UUID uuid);
}