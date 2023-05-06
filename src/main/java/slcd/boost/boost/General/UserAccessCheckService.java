package slcd.boost.boost.General;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slcd.boost.boost.SecurityConfig.JwtUtils;
import slcd.boost.boost.Users.Entities.TeamLeaderId;
import slcd.boost.boost.Users.Entities.UserEntity;
import slcd.boost.boost.Users.Repos.TeamLeadersRepository;
import slcd.boost.boost.Users.Repos.UserRepository;

import java.nio.file.AccessDeniedException;

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

    public void checkTeamLeadAccess(Long requestedUserId) throws AccessDeniedException{
        boolean isGrantedAccess = false;

        //Получение id тимлида из jwt токена
        Long currentUserId = getUserIdFromJwtToken();

        //Проверка, есть ли доступ
        if(currentUserId.equals(requestedUserId))
            isGrantedAccess = true;

        UserEntity user = userRepository.findById(requestedUserId).orElseThrow();
        UserEntity teamLeader = userRepository.findById(currentUserId).orElseThrow();

        TeamLeaderId teamLeaderId = new TeamLeaderId(user, teamLeader);

        //Проверка, есть ли у тимлида доступ
        if(teamLeadersRepository.existsById(teamLeaderId))
        if(teamLeadersRepository.existsById(teamLeaderId))
            isGrantedAccess = true;

        if (!isGrantedAccess)
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
    }

    public Long getUserIdFromJwtToken(){
        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = jwtUtils.parseJwt(authorizationHeader);
        return jwtUtils.getUserIdFromJwtToken(jwtToken);
    }
}
