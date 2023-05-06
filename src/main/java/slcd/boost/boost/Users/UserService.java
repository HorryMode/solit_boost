package slcd.boost.boost.Users;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import slcd.boost.boost.General.Interfaces.IResponse;
import slcd.boost.boost.General.Exceptions.ResourceAlreadyExistsException;
import slcd.boost.boost.General.Exceptions.ResourseNotFoundException;
import slcd.boost.boost.General.DTOs.SearchCriteria;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.UserShort;
import slcd.boost.boost.Users.DTOs.CreateLeaderShipRequest;
import slcd.boost.boost.Users.DTOs.LeaderShipInfo;
import slcd.boost.boost.Users.DTOs.UsersSearchRequest;
import slcd.boost.boost.General.DTOs.MessageResponse;
import slcd.boost.boost.Users.DTOs.AllUsersResponse;
import slcd.boost.boost.Users.DTOs.ShortUserInfoResponse;
import slcd.boost.boost.Users.DTOs.UserInfoResponse;
import slcd.boost.boost.Users.Repos.TeamLeadersRepository;
import slcd.boost.boost.Users.Repos.UserRepository;
import slcd.boost.boost.General.Constants;
import slcd.boost.boost.General.UserAccessCheckService;
import slcd.boost.boost.Users.Entities.TeamLeaderId;
import slcd.boost.boost.Users.Entities.TeamLeadersEntity;
import slcd.boost.boost.Users.Entities.UserEntity;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    //Установка формата даты
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserAccessCheckService userAccessCheckService;

    @Autowired
    private TeamLeadersRepository teamLeadersRepository;

    public ShortUserInfoResponse getShortUserInfo(Long id) throws AccessDeniedException {
        //Поиск пользователя в бд
        UserEntity user = findUserById(id);

        //Проверка, текущий ли пользователь запращивает доступ
        userAccessCheckService.checkTeamLeadAccess(id);

        //Формирование ответа
        String name = user.getLastName() + " " + user.getFirstName();
        return new ShortUserInfoResponse(name, null);
    }

    public UserInfoResponse getUserInfo(Long id) throws AccessDeniedException {
        //Поиск пользователя в БД
        UserEntity user = findUserById(id);

        //Проверка, текущий ли доступ запращивает доступ
        userAccessCheckService.checkTeamLeadAccess(id);

        //Формирование ответа
        String name = user.getLastName() + " " + user.getFirstName();
        String birthDate = user.getBirthDay().format(formatter);
        String workStartDate = user.getWorkStartDate().format(formatter);

        //Формирование информации о стаже работы
        String workExperienceString = formatWorkExperienceString(user.getWorkStartDate());

        //Формирование типа занятости
        String workType = "zero";
        if(user.getWorkTypeCode().equals("PART_TIME"))
            workType = Constants.PART_TIME_STRING;
        else if(user.getWorkTypeCode().equals("FULL_TIME"))
            workType = Constants.FULL_TIME_STRING;

        return new UserInfoResponse(
                name,
                birthDate,
                user.getPhoneNumber(),
                user.getWorkMail(),
                workStartDate,
                workExperienceString,
                user.getPost(),
                user.getLocation(),
                workType
        );
    }

    public MessageResponse addNewLeaderShips(CreateLeaderShipRequest request){
        //Поиск и создание сущности тимлидера
        Long teamLeaderId = request.getTeamLeaderId();
        UserEntity teamLeader = findUserById(teamLeaderId);

        for (LeaderShipInfo leaderShipInfo : request.getLeaderShipInfos()){

            if (Objects.equals(teamLeaderId, leaderShipInfo.getUserId()))
                throw new ResourceAlreadyExistsException(
                        Constants.USER_CANT_BE_TL_MESSAGE
                );

            UserEntity user = findUserById(leaderShipInfo.getUserId());

            //Проверка, существует ли уже такое лидерство
            TeamLeaderId id = new TeamLeaderId(user, teamLeader);

            //Если лидерство существует, вернуть ошибку
            if(teamLeadersRepository.existsById(id))
                throw new ResourceAlreadyExistsException(
                        String.format(Constants.USER_ALREADY_TL_MESSAGE, teamLeaderId, leaderShipInfo.getUserId())
                );

            //Проверка, является не пытаются назначить ли пользователя лидером своему лидеру
            id = new TeamLeaderId(teamLeader, user);

            //Если лидерство существует, вернуть ошибку
            if(teamLeadersRepository.existsById(id))
                throw new ResourceAlreadyExistsException(
                        String.format(Constants.USER_ALREADY_TL_MESSAGE, teamLeaderId, leaderShipInfo.getUserId())
                );
        }

        //Формирование списка сущностей на добавление
        ArrayList<TeamLeadersEntity> teamLeadersEntityArrayList = new ArrayList<>();

        for (LeaderShipInfo leaderShipInfo : request.getLeaderShipInfos()){
            UserEntity user = findUserById(leaderShipInfo.getUserId());

            var teamLeaderEntity = new TeamLeadersEntity();
            TeamLeaderId id = new TeamLeaderId(user, teamLeader);
            teamLeaderEntity.setId(id);
            teamLeaderEntity.setMainLeader(leaderShipInfo.isMainLeader());

            teamLeadersEntityArrayList.add(teamLeaderEntity);
        }

        for (TeamLeadersEntity entity : teamLeadersEntityArrayList){
            teamLeadersRepository.save(entity);
        }

        return new MessageResponse(
                "Лидерство успешно добавлено"
        );
    }

    public UserEntity findUserById(Long id){

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourseNotFoundException(
                        String.format(Constants.USER_NOT_FOUND_MESSAGE, id)
                ));
    }

    public UserShort findShortUserInfoById(Long id){
        UserEntity user = findUserById(id);
        String userName = user.getLastName().concat(" ").concat(user.getFirstName());

        return new UserShort(id, userName);
    }

    public List<IResponse> findUsers(UsersSearchRequest searchRequest){

        List<SearchCriteria> params = new ArrayList<>();
        String teamLeaderId = String.valueOf(userAccessCheckService.getUserIdFromJwtToken());
        params.add(new SearchCriteria("teamLeader", ":", teamLeaderId));
        if (!Objects.isNull(searchRequest.getFilterBy())){
            String fullName = searchRequest.getFilterBy().getFullName();
            //Фильтр по полному имени
            if(!Objects.isNull(fullName) && !fullName.isBlank()){
                params.add(new SearchCriteria("fullName", ":", fullName));
            }
        }
        List<UserEntity> userEntities;
        if (params.isEmpty()){
            userEntities = userRepository.findAll();
        } else {
            Specification<UserEntity> spec = null;
            for (SearchCriteria param : params) {
                if (spec == null) {
                    spec = new UserSpecification(param);
                } else {
                    spec = Specification.where(spec).and(new UserSpecification(param));
                }
            }
            userEntities = userRepository.findAll(spec);
        }

        //Формирование списка ответа
        List<IResponse> users = userEntities.stream()
                .map(userEntity -> new AllUsersResponse(
                        userEntity.getId(),
                        userEntity.getFullName(),
                        userEntity.getPhoneNumber(),
                        userEntity.getWorkStartDate().toString(),
                        userEntity.getWorkMail()
                ))
                .collect(Collectors.toList());

        return users;
    }

    public boolean existsUserByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    private String formatWorkExperienceString(LocalDate workStartDate){
        var period = Period.between(workStartDate, LocalDate.now());
        String workExperienceString = new String();


        //Формирование строки с годом
        if(period.getYears() % 10 == 1)
            workExperienceString += period.getYears() + " год";
        else if(period.getYears() % 10 == 2 || period.getYears() % 10 == 3 || period.getYears() % 10 == 4)
            workExperienceString += period.getYears() + " года";
        else if(period.getYears() % 10 == 0
                || period.getYears() % 10 == 5
                || period.getYears() % 10 == 6
                || period.getYears() % 10 == 7
                || period.getYears() % 10 == 8
                || period.getYears() % 10 == 9
                || (period.getYears() >= 10 && period.getYears() <= 20)) {
            workExperienceString += period.getYears() + " лет";
        }

        //Формирование строки с месяцем
        if( period.getMonths() % 10 == 1)
            workExperienceString += " " + period.getMonths() + " месяц";
        else if(period.getMonths() % 10 == 2 || period.getMonths() % 10 == 3 || period.getMonths() % 10 == 4)
            workExperienceString += " " + period.getMonths() + " месяца";
        else if(period.getMonths() % 10 == 0
                || period.getMonths() % 10 == 5
                || period.getMonths() % 10 == 6
                || period.getMonths() % 10 == 7
                || period.getMonths() % 10 == 8
                || period.getMonths() % 10 == 9
                || (period.getMonths() >= 10 && period.getMonths() <= 20)) {
            workExperienceString += " " + period.getMonths() + " месяцев";
        }

        //Формирование строки с месяцем
        if(period.getDays() % 10 == 1)
            workExperienceString += " " + period.getDays() + " день";
        else if(period.getDays() % 10 == 2 || period.getDays() % 10 == 3 || period.getDays() % 10 == 4)
            workExperienceString += " " + period.getDays() + " дня";
        else if(period.getDays() % 10 == 0
                || period.getDays() % 10 == 5
                || period.getDays() % 10 == 6
                || period.getDays() % 10 == 7
                || period.getDays() % 10 == 8
                || period.getDays() % 10 == 9
                || (period.getDays() >= 10 && period.getDays() <= 20)) {
            workExperienceString += " " + period.getDays() + " дней";
        }
        return workExperienceString;
    }
}
