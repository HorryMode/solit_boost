package slcd.boost.boost.SecurityConfig;

import java.io.IOException;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import slcd.boost.boost.General.DTOs.ExceptionResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        //Создание объекта ответа
        ExceptionResponse exceptionResponse
                = new ExceptionResponse(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

        //Формирование json-ответа
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(exceptionResponse);

        //Формирование ответа
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

}
