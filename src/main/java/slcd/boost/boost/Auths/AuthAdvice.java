package slcd.boost.boost.Auths;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import slcd.boost.boost.Auths.Exceptions.BadCredentialsException;
import slcd.boost.boost.Auths.Exceptions.RefreshTokenExpireException;
import slcd.boost.boost.General.DTOs.ExceptionResponse;

@ControllerAdvice(assignableTypes = {AuthController.class})
public class AuthAdvice {

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleUserNameNotFound(InsufficientAuthenticationException e){
        var response = new ExceptionResponse(401, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException e){
        var response = new ExceptionResponse(401, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RefreshTokenExpireException.class)
    public ResponseEntity<ExceptionResponse> handleRefreshTokenExpire(RefreshTokenExpireException e){
        String message = String.format(e.getMessage(), e.getRefreshToken());

        var response = new ExceptionResponse(401, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
