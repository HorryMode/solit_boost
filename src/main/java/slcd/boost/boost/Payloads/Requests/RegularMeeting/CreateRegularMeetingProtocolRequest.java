package slcd.boost.boost.Payloads.Requests.RegularMeeting;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import slcd.boost.boost.Models.UserEntity;
import slcd.boost.boost.Payloads.General.RegularMeeting.ProtocolConversationField;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateRegularMeetingProtocolRequest {
    @NonNull
    private Long ownerId;

    @NonNull
    private List<ProtocolConversationField> fields;
}
