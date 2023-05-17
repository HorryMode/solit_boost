package slcd.boost.boost.Auths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import slcd.boost.boost.Auths.DTOs.*;
import slcd.boost.boost.SecurityConfig.JwtUtils;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AesService aesService;

    public JwtResponse authenticateUser(@RequestBody Credentials credentials) {

        LoginRequest loginRequest = mapToLoginRequest(credentials);

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        //Генерирование JWT-токена
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        //Формирование информации о пользователе для системы
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new JwtResponse(jwt);
    }

    private LoginRequest mapToLoginRequest(Credentials credentials){
        try {
            String decodedString = aesService.decrypt(
                    credentials.getCredentials()
            );

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(decodedString, LoginRequest.class);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
