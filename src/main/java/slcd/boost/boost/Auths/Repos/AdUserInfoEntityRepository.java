package slcd.boost.boost.Auths.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slcd.boost.boost.Auths.Entities.AdUserInfoEntity;

@Repository
public interface AdUserInfoEntityRepository extends JpaRepository<AdUserInfoEntity, Long> {
    AdUserInfoEntity findByUuid(String uuid);
}