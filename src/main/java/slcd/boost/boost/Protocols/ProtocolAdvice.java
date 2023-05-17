package slcd.boost.boost.Protocols;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import slcd.boost.boost.General.Exceptions.ResourceNotFoundException;
import slcd.boost.boost.General.DTOs.ExceptionResponse;
import slcd.boost.boost.Protocols.Exceptions.ProtocolAlreadyHasStatusException;
import slcd.boost.boost.Protocols.Exceptions.ProtocolCannotHaveThisStatusException;
import slcd.boost.boost.Protocols.RegularMeetings.RMCConstants;
import slcd.boost.boost.Protocols.RegularMeetings.RegularMeetingController;

@RestControllerAdvice(assignableTypes = RegularMeetingController.class)
@Order(1)
public class ProtocolAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFound(ResourceNotFoundException e){
        var exceptionResponse = new ExceptionResponse(404, e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProtocolAlreadyHasStatusException.class)
    public ResponseEntity<ExceptionResponse> handleAlreadyHasStatus(ProtocolAlreadyHasStatusException e){

        String message = String.format(
                RMCConstants.PROTOCOL_ALREADY_HAS_STATUS_MESSAGE,
                e.getProtocolUuid(),
                e.getStatusName()
        );
        var exceptionResponse = new ExceptionResponse(409, message);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProtocolCannotHaveThisStatusException.class)
    public ResponseEntity<ExceptionResponse> handleCannotHaveThisStatus(ProtocolCannotHaveThisStatusException e){

        String message = String.format(
                RMCConstants.PROTOCOL_CANNOT_HAVE_THIS_STATUS_MESSAGE,
                e.getProtocolUuid(),
                e.getCurrentStatus(),
                e.getStatusName()
        );
        var exceptionResponse = new ExceptionResponse(409, message);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }
}
