package slcd.boost.boost.Auths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import slcd.boost.boost.Auths.DTOs.*;
import slcd.boost.boost.Auths.Entities.RefreshSessionEntity;
import slcd.boost.boost.SecurityConfig.JwtUtils;
import slcd.boost.boost.Users.Entities.UserEntity;
import slcd.boost.boost.Users.UserService;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AesService aesService;

    @Autowired
    private RefreshSessionService refreshSessionService;

    @Autowired
    private UserService userService;

    public JwtResponse authenticateUser(String fingerprint, Credentials credentials) {

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

        //Формирование нового рефреш-токена
        UserEntity user = userService.findUserById(userDetails.getId());
        String refreshToken = refreshSessionService.generateRefreshToken(
                fingerprint,
                user
        );

        return new JwtResponse(jwt, refreshToken);
    }

    public JwtResponse refreshUser(String fingerprint, String refreshToken) {
        UserEntity user = refreshSessionService
                .validateRefreshToken(fingerprint, refreshToken);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String newRefreshToken = refreshSessionService.generateRefreshToken(
                fingerprint,
                user
        );

        return new JwtResponse(jwt, newRefreshToken);
    }

    private LoginRequest mapToLoginRequest(Credentials credentials) {
        try {
            String decodedString = aesService.decrypt(
                    credentials.getCredentials()
            );

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(decodedString, LoginRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
