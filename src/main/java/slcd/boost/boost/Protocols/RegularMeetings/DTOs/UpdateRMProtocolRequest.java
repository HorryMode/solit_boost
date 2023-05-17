package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import slcd.boost.boost.Protocols.Interfaces.IProtocolRequest;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRMProtocolRequest implements IProtocolRequest {

    @Valid
    @NotNull
    private List<UpdateRMProtocolField> fields;
}
