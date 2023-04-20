package slcd.boost.boost.Payloads.General.RegularMeeting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProtocolConversationField {
    @NonNull
    private String summary;

    private String description;

    private Long responsibleUserId;

    private String result;

    private List<ProtocolConversationAttachment> attachments;
}
