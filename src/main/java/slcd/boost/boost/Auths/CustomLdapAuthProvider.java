package slcd.boost.boost.Auths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;
import slcd.boost.boost.Auths.DTOs.UserDetailsImpl;
import slcd.boost.boost.Auths.Exceptions.BadCredentialsException;
import slcd.boost.boost.General.Constants;
import slcd.boost.boost.Users.Entities.UserEntity;
import slcd.boost.boost.Users.UserService;

@Component
public class CustomLdapAuthProvider implements AuthenticationProvider {

    @Autowired
    private LdapContextSource contextSource;

    @Autowired
    private LdapService ldapService;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        boolean isAuthenticated = ldapService.authenticate(
                username,
                password
        );

        //Проверка, аутентифицирован ли пользователь в системе
        if(!isAuthenticated || !userService.existsUserByUsername(username))
            throw new BadCredentialsException(Constants.BAD_CREDENTIALS_MESSAGE);

        // Создаем объект UserDetails на основе данных, полученных из LDAP
        UserDetails userDetails = UserDetailsImpl.build(
                userService.findUserByUsername(username)
        );

        // Возвращаем объект аутентификации
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
