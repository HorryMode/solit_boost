package slcd.boost.boost.Users.DTOs;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoResponse {
    private String name;
    private String birthDate;
    @Email
    private String workMail;
    private String workStartDate;
    private String workExperience;
    private String post;
    private String location;
    private String workType;
}
