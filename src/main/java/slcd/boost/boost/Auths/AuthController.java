package slcd.boost.boost.Auths;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Auths.DTOs.Credentials;
import slcd.boost.boost.Auths.DTOs.LoginRequest;
import slcd.boost.boost.Auths.DTOs.JwtResponse;
import slcd.boost.boost.General.ControllerScanner;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @PostMapping("/signin")
    public JwtResponse authenticateUser(@Valid @RequestBody Credentials credentials) {
        return authService.authenticateUser(credentials);
    }
}
