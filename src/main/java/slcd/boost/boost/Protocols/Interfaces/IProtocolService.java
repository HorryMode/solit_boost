package slcd.boost.boost.Protocols.Interfaces;

import org.springframework.web.multipart.MultipartFile;
import slcd.boost.boost.General.Interfaces.IResponse;
import slcd.boost.boost.General.Interfaces.ISearchRequest;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface IProtocolService {
    IResponse createProtocol(IProtocolRequest protocol, List<MultipartFile> files) throws IOException;
    IResponse updateProtocol(IProtocolRequest protocol, List<MultipartFile> files);
    IResponse getProtocol(String uuid) throws AccessDeniedException;
    List<IResponse> getProtocols(ISearchRequest searchRequest);
    IResponse getAttachment(String uuid) throws IOException;

    void setStatusOnApproval(String uuid);

    void setStatusCreated(String uuid);

    void setStatusApproved(String uuid);
}
