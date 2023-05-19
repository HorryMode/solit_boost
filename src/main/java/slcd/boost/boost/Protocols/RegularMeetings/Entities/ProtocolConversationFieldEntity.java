package slcd.boost.boost.Protocols.RegularMeetings.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import slcd.boost.boost.Protocols.Entities.AttachmentEntity;
import slcd.boost.boost.Protocols.Entities.ProtocolEntity;
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

    @ManyToOne
    @JoinColumn(name = "protocol_id", referencedColumnName = "id")
    private ProtocolEntity protocol;

    @Column(name = "summary", columnDefinition = "text")
    private String summary;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @ManyToOne
    @JoinColumn(name = "responsible_id", referencedColumnName = "id")
    private UserEntity responsible;

    @Column(name = "result", columnDefinition = "text")
    private String result;

    @Column(length = 30)
    private String status;

    private LocalDateTime updated;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachmentEntity> attachments = new ArrayList<>();
}
