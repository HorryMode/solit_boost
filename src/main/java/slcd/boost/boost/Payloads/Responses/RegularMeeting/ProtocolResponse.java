package slcd.boost.boost.Payloads.Responses.RegularMeeting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import slcd.boost.boost.Payloads.Responses.General.ConversationField;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProtocolResponse {

    private String uuid;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String status;

    private List<ConversationField> fields;
}
