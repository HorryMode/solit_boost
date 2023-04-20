package slcd.boost.boost.Payloads.General.RegularMeeting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProtocolConversationAttachment {
    private File attachment;
}
