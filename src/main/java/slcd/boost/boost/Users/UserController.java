package slcd.boost.boost.Users;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Users.DTOs.CreateLeaderShipRequest;
import slcd.boost.boost.Users.DTOs.UsersSearchRequest;
import slcd.boost.boost.General.DTOs.MessageResponse;
import slcd.boost.boost.Users.DTOs.ShortUserInfoResponse;
import slcd.boost.boost.Users.DTOs.UserInfoResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<?> getAllUsers(@Valid @RequestBody UsersSearchRequest searchRequest) throws AccessDeniedException {
        return userService.findUsers(searchRequest);
    }

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
