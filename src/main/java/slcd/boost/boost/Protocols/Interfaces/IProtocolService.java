package slcd.boost.boost.Protocols.Interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import slcd.boost.boost.General.Interfaces.IResponse;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.ProtocolPageResponse;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface IProtocolService {
    IResponse createProtocol(IProtocolRequest protocol, List<MultipartFile> files) throws IOException;

    IResponse updateProtocol(String uuid, IProtocolRequest request, List<MultipartFile> files) throws AccessDeniedException;

    IResponse getProtocol(String uuid) throws AccessDeniedException;
    ProtocolPageResponse getProtocols(Long ownerId, Pageable pageable);
    IResponse getAttachment(String uuid) throws IOException;

    void setStatusOnApproval(String uuid) throws AccessDeniedException;

    void setStatusCreated(String uuid) throws AccessDeniedException;

    void setStatusApproved(String uuid) throws AccessDeniedException;
}
