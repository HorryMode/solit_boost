package slcd.boost.boost.Auths.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import slcd.boost.boost.Users.Entities.UserEntity;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProfileResponse {

    private Long id;
    private String fullname;

    public static ProfileResponse mapFromEntity(UserEntity user){
        return new ProfileResponse(
                user.getId(),
                user.getFullName()
        );
    }
}
