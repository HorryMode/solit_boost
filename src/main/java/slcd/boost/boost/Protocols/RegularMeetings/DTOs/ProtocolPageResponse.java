package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import slcd.boost.boost.General.Interfaces.IResponse;
import slcd.boost.boost.Protocols.Entities.ProtocolEntity;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProtocolPageResponse implements IResponse {
    private List<ShortProtocol> protocols;
    private long pageNumber;
    private long totalPages;
    private boolean first;
    private boolean last;

    public static ProtocolPageResponse mapPageEntity (Page<ProtocolEntity> page){
        return new ProtocolPageResponse(
                mapShortProtocolList(page.getContent()),
                page.getNumber(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    private static List<ShortProtocol> mapShortProtocolList(List<ProtocolEntity> protocolEntities){
        List<ShortProtocol> protocols = new ArrayList<>();
        for (ProtocolEntity protocolEntity : protocolEntities)
            protocols.add(
                    ShortProtocol.mapFromEntity(protocolEntity)
            );

        return protocols;
    }
}
