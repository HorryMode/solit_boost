package slcd.boost.boost.Protocols.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import slcd.boost.boost.Protocols.RegularMeetings.Entities.ProtocolConversationFieldEntity;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EProtocolType;
import slcd.boost.boost.Users.Entities.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "f_protocols")
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "uuid", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private UserEntity owner;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id", referencedColumnName = "id")
    private UserEntity assignedUser;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EProtocolType type;

    private LocalDateTime created;

    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private ProtocolStatusEntity status;

    @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProtocolConversationFieldEntity> fields = new ArrayList<>();
}
