package slcd.boost.boost.Users.Entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProductRepository extends JpaRepository<UserProductEntity, UserProductId> {
    List<UserProductEntity> findById_User(UserEntity user);
}