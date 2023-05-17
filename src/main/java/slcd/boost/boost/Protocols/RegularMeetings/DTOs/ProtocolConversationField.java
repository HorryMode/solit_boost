package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProtocolConversationField {

    @NotNull
    private String summary;

    private String description;

    @NotNull
    private Long responsibleUserId;

    private String result;

    @NotNull
    private String status;

    @Valid
    private List<FilePayload> attachments;
}
