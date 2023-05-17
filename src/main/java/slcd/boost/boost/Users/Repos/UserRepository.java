package slcd.boost.boost.Users.Repos;

import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Users.Entities.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    List<UserEntity> findByArchived(Boolean archived);
    Optional<UserEntity> findByUuid(UUID uuid);
    boolean existsByUuidAndArchived(UUID uuid, Boolean archived);
    boolean existsByUuid(UUID uuid);

    boolean existsByUsername(String username);
    Optional<UserEntity> findByUsername(String username);

}
