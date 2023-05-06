package slcd.boost.boost.Auths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import slcd.boost.boost.Auths.DTOs.LdapInfoDTO;
import slcd.boost.boost.Auths.Exceptions.BadCredentialsException;
import slcd.boost.boost.General.Constants;
import slcd.boost.boost.SecurityConfig.JwtUtils;
import slcd.boost.boost.Auths.DTOs.LoginRequest;
import slcd.boost.boost.Auths.DTOs.UserDetailsImpl;
import slcd.boost.boost.Auths.DTOs.JwtResponse;
import slcd.boost.boost.Users.UserService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private LdapService ldapService;

    private final static String ROLE_NOT_FOUND_MESSAGE = "Переданная роль не найдена";

    //Установка формата даты
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public JwtResponse authenticateUser(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        //Запрос на аутентификацию через Active Directory
        boolean isAuthenticated = ldapService.authenticate(
                username,
                password
        );

        //Проверка, аутентифицирован ли пользователь через Active Directory
        if(!isAuthenticated)
            throw new BadCredentialsException(Constants.BAD_CREDENTIALS_MESSAGE);

        //Проверка, есть ли зарегистрирован ли пользователь в системе
        if(!userService.existsUserByUsername(username))
            throw new BadCredentialsException(Constants.BAD_CREDENTIALS_MESSAGE);

        //Получение objectGUID и username из ActiveDirectory
        LdapInfoDTO ldapUserInfo = ldapService.findByUsername(username);
        ldapService.synchronizeAdUserInfo(ldapUserInfo);

        //Генерирование JWT-токена
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        //Формирование информации о пользователе для системы
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList();

        return new JwtResponse(jwt);
    }
}
