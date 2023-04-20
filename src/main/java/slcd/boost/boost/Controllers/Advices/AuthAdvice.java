package slcd.boost.boost.Controllers.Advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import slcd.boost.boost.Controllers.Controllers.AuthController;
import slcd.boost.boost.Payloads.Responses.General.ExceptionResponse;

@ControllerAdvice(assignableTypes = {AuthController.class})
public class AuthAdvice {

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleUserNameNotFound(InsufficientAuthenticationException e){
        var response = new ExceptionResponse(401, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
