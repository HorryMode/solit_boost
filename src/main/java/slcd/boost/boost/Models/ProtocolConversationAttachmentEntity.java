package slcd.boost.boost.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "f_protocol_conversation_attachments")
public class ProtocolConversationAttachmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    private ProtocolConversationFieldEntity field;

    @Lob
    private byte[] attachment;

    public ProtocolConversationAttachmentEntity(ProtocolConversationFieldEntity field, byte[] attachment) {
        this.field = field;
        this.attachment = attachment;
    }
}
