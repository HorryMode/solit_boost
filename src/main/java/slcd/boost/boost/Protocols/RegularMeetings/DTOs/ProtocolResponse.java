package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import slcd.boost.boost.General.Interfaces.IResponse;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProtocolResponse implements IResponse {

    private String uuid;

    private String name;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String status;

    private List<ConversationField> fields;
}
