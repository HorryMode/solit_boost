package slcd.boost.boost.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import slcd.boost.boost.Models.Enum.EProtocolType;

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
    @Column(name = "uuid", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID uuid;

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

    public ProtocolEntity(UUID uuid, UserEntity owner, UserEntity assignedUser, EProtocolType type, LocalDateTime created, LocalDateTime updated, ProtocolStatusEntity status) {
        this.uuid = uuid;
        this.owner = owner;
        this.assignedUser = assignedUser;
        this.type = type;
        this.created = created;
        this.updated = updated;
        this.status = status;
    }
}
