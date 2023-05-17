package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import slcd.boost.boost.General.DTOs.Attachment;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties
public class UpdateRMProtocolField {

    @NotNull
    private String uuid;

    @NotNull
    private String summary;

    private String description;

    @NotNull
    private Long responsibleUserId;

    @NotNull
    private String status;

    private String result;

    @Valid
    private List<Attachment> attachments;
}
