package slcd.boost.boost.Users.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import slcd.boost.boost.Syncs.SyncService;
import slcd.boost.boost.Users.Entities.ProductEntity;
import slcd.boost.boost.Users.Entities.UserEntity;
import slcd.boost.boost.Users.Entities.UserProductEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductUsersResponse {
    private String name;
    private List<AllUsersResponse> users;

    public static ProductUsersResponse mapFromEntity(ProductEntity product, Predicate<UserProductEntity> predicate){
        List<AllUsersResponse> users = new ArrayList<>();
        System.out.println(product.toString());
        product.getUserProducts()
                .stream()
                .filter(predicate)
                .forEach(
                userProductEntity -> {
                    UserEntity userEntity = userProductEntity.getId().getUser();
                    AllUsersResponse user = new AllUsersResponse(
                        userEntity.getId(),
                            userEntity.getFullName(),
                            userEntity.getWorkStartDate(),
                            userEntity.getEmail()
                    );
                    users.add(user);
                }
        );
        return new ProductUsersResponse(
                product.getName(),
                users
        );
    }
}
