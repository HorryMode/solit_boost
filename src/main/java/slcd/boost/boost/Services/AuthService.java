package slcd.boost.boost.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import slcd.boost.boost.Configs.SecurityConfig.JwtUtils;
import slcd.boost.boost.Models.Enum.ERole;
import slcd.boost.boost.Models.Exceptions.ResourseNotFoundException;
import slcd.boost.boost.Models.RoleEntity;
import slcd.boost.boost.Models.UserEntity;
import slcd.boost.boost.Payloads.Requests.Auth.LoginRequest;
import slcd.boost.boost.Payloads.Requests.Auth.SignupRequest;
import slcd.boost.boost.Payloads.Requests.Auth.UserDetailsImpl;
import slcd.boost.boost.Payloads.Responses.Auth.JwtResponse;
import slcd.boost.boost.Payloads.Responses.General.MessageResponse;
import slcd.boost.boost.Repositories.RoleRepository;
import slcd.boost.boost.Repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    private final static String ROLE_NOT_FOUND_MESSAGE = "Переданная роль не найдена";

    //Установка формата даты
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public JwtResponse authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        return new JwtResponse(jwt);
    }

    public MessageResponse registerUser(SignupRequest signUpRequest) {

        //Проверка, существует ли пользователь
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }

        //Создание нового пользователя
        //Заполнение параметров пользователя
        UserEntity user = new UserEntity(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<RoleEntity> roles = new HashSet<>();

        if (strRoles == null) {
            RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new ResourseNotFoundException(ROLE_NOT_FOUND_MESSAGE));
            roles.add(userRole);
        }
        else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "head" -> {
                        RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_DEPARTMENT_HEAD)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
                        roles.add(adminRole);
                    }
                    case "tl" -> {
                        RoleEntity modRole = roleRepository.findByName(ERole.ROLE_TEAM_LEADER)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
                        roles.add(modRole);
                    }
                    case "admin" -> {
                        RoleEntity modRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
                        roles.add(modRole);
                    }
                    default -> {
                        RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
                        roles.add(userRole);
                    }
                }
            });
        }

        user.setLastName(signUpRequest.getLastName());
        user.setFirstName(signUpRequest.getFirstName());
        if(Objects.nonNull(user.getSecondName())) {
            user.setUsername(signUpRequest.getSecondName());
        }
        LocalDate formattedBirthDay = LocalDate.parse(signUpRequest.getBirthDay(), formatter);
        user.setBirthDay(formattedBirthDay);

        user.setGenderCode(signUpRequest.getGenderCode());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setWorkMail(signUpRequest.getWorkMail());

        LocalDate formattedWorkStartDate = LocalDate.parse(signUpRequest.getWorkStartDate(), formatter);
        user.setWorkStartDate(formattedWorkStartDate);

        user.setPost(signUpRequest.getPost());
        user.setRegistered(LocalDateTime.now());
        user.setArchived(false);
        user.setRoles(roles);
        user.setLocation(signUpRequest.getLocation());
        user.setWorkTypeCode(signUpRequest.getWorkTypeCode());

        //Сохранение пользователя в БД
        userRepository.save(user);

        return new MessageResponse("Пользователь успешно зарегистрирован");
    }
}
