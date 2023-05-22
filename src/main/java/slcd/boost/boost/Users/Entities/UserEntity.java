package slcd.boost.boost.Users.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import slcd.boost.boost.General.Entities.PostEntity;
import slcd.boost.boost.General.Entities.SubdivisionEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "f_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "f_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    private String fullName;

    private String firstname;

    private String middlename;

    private String lastname;

    private LocalDate birthDay;


    private String genderCode;

    @Email
    private String email;

    private LocalDate workStartDate;

    @ManyToOne
    @JoinColumn(name = "subdivision_id", referencedColumnName = "id")
    private SubdivisionEntity subdivision;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private PostEntity post;

    private String location;

    private String rate;

    private LocalDateTime registered;
    private LocalDateTime updated;

    private Boolean archived;

    @OneToMany(mappedBy = "user")
    private List<TeamLeadersEntity> teamLeaders;

    @OneToMany(mappedBy = "user")
    private List<UserProductEntity> userProducts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return Objects.equals(username, that.username) && Objects.equals(uuid, that.uuid) && Objects.equals(fullName, that.fullName) && Objects.equals(firstname, that.firstname) && Objects.equals(middlename, that.middlename) && Objects.equals(lastname, that.lastname) && Objects.equals(birthDay, that.birthDay) && Objects.equals(genderCode, that.genderCode) && Objects.equals(email, that.email) && Objects.equals(workStartDate, that.workStartDate) && Objects.equals(subdivision, that.subdivision) && Objects.equals(post, that.post) && Objects.equals(location, that.location) && Objects.equals(rate, that.rate) && Objects.equals(registered, that.registered) && Objects.equals(updated, that.updated) && Objects.equals(archived, that.archived) && Objects.equals(userProducts, that.userProducts);
    }
}
