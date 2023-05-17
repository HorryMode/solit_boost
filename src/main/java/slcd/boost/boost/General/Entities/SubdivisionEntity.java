package slcd.boost.boost.General.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import slcd.boost.boost.Users.Entities.UserEntity;

import java.util.List;
import java.util.UUID;

@Entity(name = "s_subdivisions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubdivisionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "uuid", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "archived", nullable = false)
    private boolean archived;
}
