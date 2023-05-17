package slcd.boost.boost.General;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import slcd.boost.boost.SecurityConfig.JwtUtils;
import slcd.boost.boost.Users.Entities.TeamLeaderId;
import slcd.boost.boost.Users.Entities.UserEntity;
import slcd.boost.boost.Users.Repos.TeamLeadersRepository;
import slcd.boost.boost.Users.Repos.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Service
public class UserAccessCheckService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamLeadersRepository teamLeadersRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtils jwtUtils;

    private static final String ACCESS_DENIED_MESSAGE = "У пользователя нет прав для выполнения запроса";

    public void checkUserAccess(Long requestedUserId) throws AccessDeniedException {
        //Получение текущего пользователя из jwt токена
        Long currentUserId = getUserIdFromJwtToken();

        //Проверка, есть ли доступ
        if(!currentUserId.equals(requestedUserId)){
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    public void checkAccess() throws AccessDeniedException {
        boolean isGrantedAccess = false;
        if(isTeamLeader())
            isGrantedAccess = true;
        if(isSubdivisionHead())
            isGrantedAccess = true;
        if(isAdmin())
            isGrantedAccess = true;

        if (!isGrantedAccess)
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
    }

    public void checkTeamLeadAccess(Long requestedUserId) throws AccessDeniedException{
        boolean isGrantedAccess = false;

        //Получение id тимлида из jwt токена
        Long currentUserId = getUserIdFromJwtToken();

        //Проверка, есть ли доступ
        if(currentUserId.equals(requestedUserId))
            isGrantedAccess = true;

        UserEntity requestedUser = userRepository.findById(requestedUserId).orElseThrow();
        UserEntity currentUser = userRepository.findById(currentUserId).orElseThrow();

        if(isTeamLeader(requestedUser, currentUser))
            isGrantedAccess = true;

        if(isAdmin())
            isGrantedAccess = true;

        if(isSubdivisionHead(requestedUser, currentUser))
            isGrantedAccess = true;

        if (!isGrantedAccess)
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
    }

    public Long getUserIdFromJwtToken(){
        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = jwtUtils.parseJwt(authorizationHeader);
        return jwtUtils.getUserIdFromJwtToken(jwtToken);
    }

    public boolean isAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.toString()
                    .equals("ROLE_ADMIN"))
                return true;
        }
        return false;
    }

    public boolean isSubdivisionHead(Long requestedUserId){
        //Получение id тимлида из jwt токена
        Long currentUserId = getUserIdFromJwtToken();

        UserEntity requestedUser = userRepository.findById(requestedUserId).orElseThrow();
        UserEntity currentUser = userRepository.findById(currentUserId).orElseThrow();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (!grantedAuthority.toString()
                    .equals("ROLE_DEPARTMENT_HEAD"))
                continue;

            if (requestedUser.getSubdivision().getUuid()
                    .equals(currentUser.getSubdivision().getUuid()))
                return true;
        }
        return false;
    }

    private boolean isSubdivisionHead(UserEntity requestedUser, UserEntity subdivisionHead){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (!grantedAuthority.toString()
                    .equals("ROLE_DEPARTMENT_HEAD"))
                continue;

            if (requestedUser.getSubdivision().getUuid()
                    .equals(subdivisionHead.getSubdivision().getUuid()))
                return true;
        }
        return false;
    }

    public boolean isTeamLeader(){
        //Получение id тимлида из jwt токена
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.toString()
                    .equals("ROLE_TEAM_LEADER"))
                return true;
        }
        return false;
    }

    public boolean isSubdivisionHead(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.toString()
                    .equals("ROLE_DEPARTMENT_HEAD"))
                return true;
        }
        return false;
    }

    public boolean isTeamLeader(UserEntity requestedUser, UserEntity currentUser){
        TeamLeaderId teamLeaderId = new TeamLeaderId(requestedUser, currentUser);

        //Проверка, есть ли у тимлида доступ
        return teamLeadersRepository.existsById(teamLeaderId);
    }
}
