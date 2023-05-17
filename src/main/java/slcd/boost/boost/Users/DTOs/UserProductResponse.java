package slcd.boost.boost.Users.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import slcd.boost.boost.Users.Entities.UserProductEntity;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProductResponse {
    private String productName;
    private String role;
    private Long busy;

    public static UserProductResponse mapFromEntity(UserProductEntity userProductEntity){
        return new UserProductResponse(
                userProductEntity.getId().getProduct().getName(),
                userProductEntity.getRole(),
                userProductEntity.getBusy()
        );
    }
}
