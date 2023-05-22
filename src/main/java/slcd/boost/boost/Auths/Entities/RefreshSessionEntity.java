package slcd.boost.boost.Auths.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import slcd.boost.boost.Users.Entities.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "refresh_sessions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    @Column(name = "refresh_token", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID refreshToken;

    @Column(name = "fingerprint", nullable = false)
    private String fingerprint;

    @Column(name = "expiresIn", nullable = false)
    private LocalDateTime expires;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
