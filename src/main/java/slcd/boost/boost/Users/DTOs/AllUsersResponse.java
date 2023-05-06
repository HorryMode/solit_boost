package slcd.boost.boost.Users.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import slcd.boost.boost.General.Interfaces.IResponse;

@Data
@AllArgsConstructor
public class AllUsersResponse implements IResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String startWorkDate;
    private String workMail;
}
