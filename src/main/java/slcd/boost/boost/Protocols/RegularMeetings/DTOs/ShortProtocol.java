package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import slcd.boost.boost.Protocols.Entities.ProtocolEntity;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShortProtocol {
    private String uuid;
    private String name;

    public static ShortProtocol mapFromEntity(ProtocolEntity protocolEntity){
        return new ShortProtocol(
                protocolEntity.getUuid().toString(),
                protocolEntity.getName()
        );
    }
}
