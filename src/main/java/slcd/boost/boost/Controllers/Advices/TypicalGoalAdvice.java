package slcd.boost.boost.Controllers.Advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import slcd.boost.boost.Annotations.TypicalGoalExceptionHandler;
import slcd.boost.boost.Models.ExceptionResponse;

@ControllerAdvice(annotations = TypicalGoalExceptionHandler.class)
public class TypicalGoalAdvice {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> missingRequestBodyException(HttpMessageNotReadableException e){
        var response = new ExceptionResponse(400,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> fieldNullException(MethodArgumentNotValidException e){
        var response = new ExceptionResponse(400,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
