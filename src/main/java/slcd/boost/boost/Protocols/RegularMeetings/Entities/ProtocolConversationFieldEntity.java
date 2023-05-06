package slcd.boost.boost.Protocols.RegularMeetings.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import slcd.boost.boost.Protocols.Entities.ProtocolConversationAttachmentEntity;
import slcd.boost.boost.Protocols.Entities.ProtocolEntity;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EConversationFieldStatus;
import slcd.boost.boost.Users.Entities.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "f_protocol_conversation_fields")
public class ProtocolConversationFieldEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "uuid", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID uuid;

    private Long rowNumber;

    @ManyToOne
    @JoinColumn(name = "protocol_id", referencedColumnName = "id")
    private ProtocolEntity protocol;

    private String summary;

    private String description;

    @ManyToOne
    @JoinColumn(name = "responsible_id", referencedColumnName = "id")
    private UserEntity responsible;

    private String result;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EConversationFieldStatus status;

    private LocalDateTime updated;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProtocolConversationAttachmentEntity> attachments = new ArrayList<>();
}
