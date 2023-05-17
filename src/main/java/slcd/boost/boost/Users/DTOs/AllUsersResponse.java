package slcd.boost.boost.Users.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import slcd.boost.boost.General.Interfaces.IResponse;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AllUsersResponse implements IResponse {
    private Long id;
    private String fullName;
    private LocalDate startWorkDate;
    private String workMail;
}
