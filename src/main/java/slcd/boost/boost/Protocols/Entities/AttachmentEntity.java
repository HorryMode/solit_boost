package slcd.boost.boost.Protocols.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import slcd.boost.boost.Protocols.RegularMeetings.Entities.ProtocolConversationFieldEntity;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "f_attachments")
public class AttachmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    private ProtocolConversationFieldEntity field;

    private UUID uuid;

    private String name;

    private String extension;
}
