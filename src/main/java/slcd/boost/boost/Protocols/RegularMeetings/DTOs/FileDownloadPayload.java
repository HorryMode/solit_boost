package slcd.boost.boost.Protocols.RegularMeetings.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.UrlResource;
import slcd.boost.boost.General.Interfaces.IResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadPayload implements IResponse {
    private UrlResource file;
    private String fileName;
}
