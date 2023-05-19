package slcd.boost.boost.Syncs;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import slcd.boost.boost.Users.DTOs.*;
import slcd.boost.boost.General.DTOs.MessageResponse;
import slcd.boost.boost.Users.UserService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/syncs")
public class SyncController {

    @Autowired
    private SyncService syncService;
}

