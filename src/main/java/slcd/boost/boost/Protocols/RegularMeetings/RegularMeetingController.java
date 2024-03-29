package slcd.boost.boost.Protocols.RegularMeetings;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.*;
import slcd.boost.boost.General.DTOs.UUIDResponse;

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
    public UUIDResponse createProtocol(
            @RequestPart(name = "protocol") @Valid String protocolRequest,
            @RequestPart(name = "files", required = false)List<MultipartFile> files
    )
            throws IOException {
        CreateRegularMeetingProtocolRequest protocol
                = new ObjectMapper().readValue(protocolRequest, CreateRegularMeetingProtocolRequest.class);

        return (UUIDResponse) regularMeetingService.createProtocol(protocol, files);
    }

    @PostMapping("/protocols/{uuid}")
    @ResponseStatus(HttpStatus.CREATED)
    public UUIDResponse update(
            @PathVariable String uuid,
            @RequestPart(name = "protocol") @Valid String protocolRequest,
            @RequestPart(name = "files", required = false)List<MultipartFile> files
    )
            throws IOException {

        UpdateRMProtocolRequest protocol
                = new ObjectMapper().readValue(protocolRequest, UpdateRMProtocolRequest.class);

        return (UUIDResponse) regularMeetingService.updateProtocol(uuid, protocol, files);
    }

    @GetMapping("/protocols/{protocolUuid}")
    @ResponseStatus(HttpStatus.OK)
    public ProtocolResponse getProtocol(@PathVariable @Valid String protocolUuid) throws AccessDeniedException {
        return (ProtocolResponse) regularMeetingService.getProtocol(protocolUuid);
    }

    @GetMapping("/protocols")
    @ResponseStatus(HttpStatus.OK)
    public ProtocolPageResponse getProtocols(
            @RequestParam("ownerId") Long ownerId,
            Pageable pageable
    ) {
        return regularMeetingService.getProtocols(ownerId, pageable);
    }

    @PostMapping("/protocols/{uuid}/setStatusOnApproval")
    @ResponseStatus(HttpStatus.OK)
    public void setStatusOnApproval(@PathVariable String uuid) throws AccessDeniedException {
        regularMeetingService.setStatusOnApproval(uuid);
    }

    @PostMapping("/protocols/{uuid}/setStatusCreated")
    @ResponseStatus(HttpStatus.OK)
    public void setStatusCreated(@PathVariable String uuid) throws AccessDeniedException {
        regularMeetingService.setStatusCreated(uuid);
    }

    @PostMapping("/protocols/{uuid}/setStatusApproved")
    @ResponseStatus(HttpStatus.OK)
    public void setStatusApproved(@PathVariable String uuid) throws AccessDeniedException {
        regularMeetingService.setStatusApproved(uuid);
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
