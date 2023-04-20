package slcd.boost.boost.Payloads.Responses.General;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConversationField {
    private Long id;
    private String summary;
    private String description;
    private String responsibleUserId;
    private String result;
    private String status;
    private List<Attachment> attachments;
}
