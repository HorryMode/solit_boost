package slcd.boost.boost.Configs.SecurityConfig;

import java.io.IOException;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import slcd.boost.boost.Payloads.Responses.General.ExceptionResponse;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
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
