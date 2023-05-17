package slcd.boost.boost.Users;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import slcd.boost.boost.General.Exceptions.ResourceAlreadyExistsException;
import slcd.boost.boost.General.Exceptions.ResourceNotFoundException;
import slcd.boost.boost.General.DTOs.ExceptionResponse;

@ControllerAdvice(assignableTypes = UserController.class)
@Order(2)
public class UserAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFound(ResourceNotFoundException e){

        var exceptionResponse = new ExceptionResponse(404, e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException e){
        var exceptionResponse = new ExceptionResponse(409, e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }
}
