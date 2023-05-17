package slcd.boost.boost.Users.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import slcd.boost.boost.Users.Entities.ProductEntity;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductShortResponse {
    private String name;

    public static ProductShortResponse mapFromEntity(ProductEntity productEntity){
        return new ProductShortResponse(productEntity.getName());
    }
}
