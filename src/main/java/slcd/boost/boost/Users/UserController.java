package slcd.boost.boost.Users;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Users.DTOs.*;
import slcd.boost.boost.General.DTOs.MessageResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}/products")
    @ResponseStatus(HttpStatus.OK)
    public List<UserProductResponse> getUserProducts(@Valid @PathVariable(name = "id") Long id)
            throws AccessDeniedException{
        return userService.findUserProduct(id);
    }

    @GetMapping("/{id}/short")
    @ResponseStatus(HttpStatus.OK)
    public ShortUserInfoResponse getShortUserInfo(@Valid @PathVariable(name = "id") Long id) throws AccessDeniedException {
        return userService.getShortUserInfo(id);
    }

    @GetMapping("/{id}")
    public UserInfoResponse getUserInfo(@Valid @PathVariable(name = "id") Long id) throws AccessDeniedException {
        return userService.getUserInfo(id);
    }
}
