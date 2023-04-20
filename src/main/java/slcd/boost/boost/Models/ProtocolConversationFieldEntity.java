package slcd.boost.boost.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import slcd.boost.boost.Models.Enum.EConversationFieldStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "protocol_uuid", referencedColumnName = "uuid")
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

    public ProtocolConversationFieldEntity(ProtocolEntity protocol, String summary, String description, UserEntity responsible, String result, EConversationFieldStatus status, LocalDateTime updated) {
        this.protocol = protocol;
        this.summary = summary;
        this.description = description;
        this.responsible = responsible;
        this.result = result;
        this.status = status;
        this.updated = updated;
    }
}
