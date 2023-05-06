package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import slcd.boost.boost.Protocols.Interfaces.IProtocolRequest;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateRegularMeetingProtocolRequest implements IProtocolRequest {
    @NotNull
    private Long ownerId;

    @Valid
    private List<ProtocolConversationField> fields;
}
