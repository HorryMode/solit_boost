package slcd.boost.boost.Protocols.RegularMeetings;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.FileDownloadPayload;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.CreateRegularMeetingProtocolRequest;
import slcd.boost.boost.General.DTOs.UUIDResponse;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.ProtocolResponse;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/RegularMeetings")
public class RegularMeetingController {

    @Autowired
    private RegularMeetingService regularMeetingService;

    @Autowired
    private HttpServletResponse response;

    @PostMapping("/protocols")
    @ResponseStatus(HttpStatus.CREATED)
    public UUIDResponse createProtocol(@RequestPart(name = "protocol") @Valid String protocolRequest,
                                    @RequestPart(name = "files", required = false)List<MultipartFile> files)
            throws IOException {

        CreateRegularMeetingProtocolRequest protocol
                = new ObjectMapper().readValue(protocolRequest, CreateRegularMeetingProtocolRequest.class);

        return (UUIDResponse) regularMeetingService.createProtocol(protocol, files);
    }

    @GetMapping("/protocols/{protocolUuid}")
    @ResponseStatus(HttpStatus.OK)
    public ProtocolResponse getProtocol(@PathVariable @Valid String protocolUuid) throws AccessDeniedException {
        return (ProtocolResponse) regularMeetingService.getProtocol(protocolUuid);
    }

    @GetMapping("/attachments/{attachmentUuid}")
    @ResponseStatus(HttpStatus.OK)
    public Resource getAttachment(@PathVariable @Valid String attachmentUuid) throws IOException {

        //Получене файла и его названия
        var fileDownloadPayload = (FileDownloadPayload) regularMeetingService.getAttachment(attachmentUuid);

        //Установка хэдеров
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileDownloadPayload.getFileName() +"\"");
        response.setContentType("application/octet-stream");

        return fileDownloadPayload.getFile();
    }
}
