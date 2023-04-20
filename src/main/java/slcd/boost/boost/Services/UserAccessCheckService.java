package slcd.boost.boost.Services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Configs.SecurityConfig.JwtUtils;
import slcd.boost.boost.Models.TeamLeaderId;
import slcd.boost.boost.Models.TeamLeadersEntity;
import slcd.boost.boost.Models.UserEntity;
import slcd.boost.boost.Repositories.TeamLeadersRepository;
import slcd.boost.boost.Repositories.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public  void checkTeamLeadAccess(Long requestedUserId) throws AccessDeniedException{
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

    private Long getUserIdFromJwtToken(){
        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = jwtUtils.parseJwt(authorizationHeader);
        return jwtUtils.getUserIdFromJwtToken(jwtToken);
    }
}
