package slcd.boost.boost.Users.DTOs;

import lombok.Data;
import slcd.boost.boost.General.Interfaces.ISearchRequest;

@Data
public class UsersSearchRequest implements ISearchRequest {
    private UserFilterFields filterBy;
    private UserSort sortBy;
}



