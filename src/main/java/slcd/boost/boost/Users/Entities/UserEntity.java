package slcd.boost.boost.Users.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "f_users", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String hashedPassword;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "f_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    private String fullName;

    private String lastName;

    private String firstName;

    private String secondName;

    private LocalDate birthDay;

    private String phoneNumber;

    private String genderCode;

    @Email
    private String workMail;

    private LocalDate workStartDate;

    private String post;

    private String location;

    private String workTypeCode;

    private LocalDateTime registered;

    private Boolean archived;

    @OneToMany(mappedBy = "user")
    private List<TeamLeadersEntity> teamLeaders;


    public UserEntity(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }
}
