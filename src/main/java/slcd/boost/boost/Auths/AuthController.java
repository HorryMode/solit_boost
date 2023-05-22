package slcd.boost.boost.Auths;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Auths.DTOs.*;
import slcd.boost.boost.General.ControllerScanner;
import slcd.boost.boost.Users.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public JwtResponse authenticateUser(
            @RequestHeader("X-Fingerprint") String fingerprint,
            @Valid @RequestBody Credentials credentials
    ) {
        return authService.authenticateUser(fingerprint, credentials);
    }

    @PostMapping("/refresh")
    public JwtResponse refreshUser(
            @RequestHeader("X-Fingerprint") String fingerprint,
            @RequestBody RefreshTokenRequest refreshToken
    ){
        return authService.refreshUser(fingerprint, refreshToken.getRefreshToken());
    }

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ProfileResponse getProfile(){
        return userService.findShortUserInfoById();
    }
}
