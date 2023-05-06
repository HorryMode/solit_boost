package slcd.boost.boost.General.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import slcd.boost.boost.General.Interfaces.IResponse;

@AllArgsConstructor
@Data
public class UUIDResponse implements IResponse {
    private String uuid;
}
