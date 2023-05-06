package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import slcd.boost.boost.General.DTOs.Attachment;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConversationField {
    private String uuid;
    private String summary;
    private String description;
    private UserShort responsibleUser;
    private String result;
    private String status;
    private List<Attachment> attachments;
}
