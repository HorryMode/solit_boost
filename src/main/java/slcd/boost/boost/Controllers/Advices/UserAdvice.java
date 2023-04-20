package slcd.boost.boost.Controllers.Advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import slcd.boost.boost.Controllers.Controllers.UserController;
import slcd.boost.boost.Models.Exceptions.ResourceAlreadyExistsException;
import slcd.boost.boost.Models.Exceptions.ResourseNotFoundException;
import slcd.boost.boost.Payloads.Responses.General.ExceptionResponse;

@ControllerAdvice(assignableTypes = UserController.class)
public class UserAdvice {

    @ExceptionHandler(ResourseNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFound(ResourseNotFoundException e){
        String message = "Пользователь с id " + e.getMessage() + " не найден";

        var exceptionResponse = new ExceptionResponse(404, message);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException e){
        var exceptionResponse = new ExceptionResponse(409, e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }
}
