package slcd.boost.boost.Protocols.RegularMeetings;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import slcd.boost.boost.General.Exceptions.ResourseNotFoundException;
import slcd.boost.boost.General.DTOs.ExceptionResponse;

@ControllerAdvice(assignableTypes = {RegularMeetingController.class})
public class RegularMeetingAdvice {

    @ExceptionHandler(ResourseNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFound(ResourseNotFoundException e){
        var exceptionResponse = new ExceptionResponse(404, e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }
}
