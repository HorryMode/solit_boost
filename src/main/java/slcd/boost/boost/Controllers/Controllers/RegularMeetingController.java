package slcd.boost.boost.Controllers.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Models.ProtocolEntity;
import slcd.boost.boost.Payloads.Requests.RegularMeeting.CreateRegularMeetingProtocolRequest;
import slcd.boost.boost.Payloads.Responses.General.UUIDResponse;
import slcd.boost.boost.Payloads.Responses.RegularMeeting.ProtocolResponse;
import slcd.boost.boost.Services.RegularMeetingService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/RegularMeetings")
public class RegularMeetingController {

    @Autowired
    private RegularMeetingService regularMeetingService;

    @PostMapping("/createProtocol")
    @ResponseStatus(HttpStatus.CREATED)
    public UUIDResponse createProtocol(@RequestBody CreateRegularMeetingProtocolRequest protocolRequest)
            throws IOException {

        return regularMeetingService.createProtocol(protocolRequest);
    }

    @GetMapping("/getProtocol")
    @ResponseStatus(HttpStatus.OK)
    public ProtocolResponse getProtocol(@RequestParam String uuid){
        return regularMeetingService.getProtocolByUUID(uuid);
    }
}
