package slcd.boost.boost.Controllers.Controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Payloads.Requests.User.CreateLeaderShipRequest;
import slcd.boost.boost.Payloads.Responses.General.MessageResponse;
import slcd.boost.boost.Payloads.Responses.User.ShortUserInfoResponse;
import slcd.boost.boost.Payloads.Responses.User.UserInfoResponse;
import slcd.boost.boost.Services.UserService;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/getShortUserInfo")
    @ResponseStatus(HttpStatus.OK)
    public ShortUserInfoResponse getShortUserInfo(@Valid @RequestParam Long id) throws AccessDeniedException {
        return userService.getShortUserInfo(id);
    }

    @GetMapping("/getUserInfo")
    public UserInfoResponse getUserInfo(@Valid @RequestParam Long id) throws AccessDeniedException {
        return userService.getUserInfo(id);
    }

    @PostMapping("/addNewLeaderShips")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_DEPARTMENT_HEAD')")
    public MessageResponse addNewLeaderShips(@Valid @RequestBody CreateLeaderShipRequest request){
        return userService.addNewLeaderShips(request);
    }
}
