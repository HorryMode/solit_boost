package slcd.boost.boost.Payloads.Requests.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import slcd.boost.boost.Models.RoleEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
    @NotBlank
    @Size(min = 6, max = 20)
    private String username;

    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    private String lastName;

    @NotBlank
    private String firstName;

    private String secondName;

    @NotBlank
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String birthDay;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String genderCode;

    @Email
    @NotBlank
    private String workMail;

    @NotBlank
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String workStartDate;

    @NotBlank
    private String post;

    @NotBlank
    private String workTypeCode;

    @NotBlank
    private String location;
}
