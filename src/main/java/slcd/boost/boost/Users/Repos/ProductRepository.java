package slcd.boost.boost.Users.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Users.Entities.ProductEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByUuidAndArchived(UUID uuid, boolean archived);
    boolean existsByUuid(UUID uuid);
    List<ProductEntity> findByArchived(boolean archived);
    Optional<ProductEntity> findByUuid(UUID uuid);
}