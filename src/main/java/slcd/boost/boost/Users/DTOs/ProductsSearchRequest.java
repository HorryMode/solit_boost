package slcd.boost.boost.Users.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import slcd.boost.boost.General.Interfaces.ISearchRequest;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsSearchRequest implements ISearchRequest {
    private List<String> products;
}
