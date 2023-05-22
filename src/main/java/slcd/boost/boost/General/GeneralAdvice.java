package slcd.boost.boost.General;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import slcd.boost.boost.General.DTOs.ExceptionResponse;
import slcd.boost.boost.General.Exceptions.OnlyTeamLeaderHaveAccessException;

import java.nio.file.AccessDeniedException;
import java.util.List;

@ControllerAdvice
public class GeneralAdvice {
     @ExceptionHandler(MethodArgumentNotValidException.class)
     ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e){
        List<String> missingFields = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .toList();

        String message = "Не переданные поля: " + String.join(", ", missingFields);

        var response = new ExceptionResponse(400, message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<ExceptionResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException e){
        String parameterName = e.getParameterName();

        String message = "Необходимый параметр '" + parameterName + "' не передан";

        var response = new ExceptionResponse(400, message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ExceptionResponse> handleAccessDenied(AccessDeniedException e){
         var response = new ExceptionResponse(403, e.getMessage());
         return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    };

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException e){
        e.printStackTrace();
        var response = new ExceptionResponse(400, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = OnlyTeamLeaderHaveAccessException.class)
    public ResponseEntity<ExceptionResponse> handleOnlyTeamLeaderAccess(OnlyTeamLeaderHaveAccessException e){
        e.printStackTrace();
        var exceptionResponse = new ExceptionResponse(403, Constants.ONLY_TEAM_LEADER_HAVE_ACCESS_MESSAGE);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionResponse> handleException(Exception e){
        e.printStackTrace();
        var response = new ExceptionResponse(500, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
