package slcd.boost.boost.Controllers.Controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Payloads.Requests.Auth.LoginRequest;
import slcd.boost.boost.Payloads.Responses.Auth.JwtResponse;
import slcd.boost.boost.Payloads.Responses.General.MessageResponse;
import slcd.boost.boost.Services.AuthService;
import slcd.boost.boost.Payloads.Requests.Auth.SignupRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public JwtResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_DEPARTMENT_HEAD')")
    public MessageResponse registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.registerUser(signupRequest);
    }
}
