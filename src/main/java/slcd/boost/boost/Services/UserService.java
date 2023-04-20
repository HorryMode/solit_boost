package slcd.boost.boost.Services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Models.Exceptions.ResourceAlreadyExistsException;
import slcd.boost.boost.Models.Exceptions.ResourseNotFoundException;
import slcd.boost.boost.Models.TeamLeaderId;
import slcd.boost.boost.Models.TeamLeadersEntity;
import slcd.boost.boost.Models.UserEntity;
import slcd.boost.boost.Payloads.Requests.User.CreateLeaderShipRequest;
import slcd.boost.boost.Payloads.Requests.User.LeaderShipInfo;
import slcd.boost.boost.Payloads.Responses.General.MessageResponse;
import slcd.boost.boost.Payloads.Responses.User.ShortUserInfoResponse;
import slcd.boost.boost.Payloads.Responses.User.UserInfoResponse;
import slcd.boost.boost.Repositories.TeamLeadersRepository;
import slcd.boost.boost.Repositories.UserRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class UserService {

    private static final String PART_TIME_STRING = "Частичная занятость(4ч)";
    private static final String FULL_TIME_STRING = "Полная занятость(8ч)";
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
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourseNotFoundException(id.toString()));

        //Проверка, текущий ли пользователь запращивает доступ
        userAccessCheckService.checkTeamLeadAccess(id);

        //Формирование ответа
        String name = user.getLastName() + " " + user.getFirstName();
        return new ShortUserInfoResponse(name, null);
    }

    public UserInfoResponse getUserInfo(Long id) throws AccessDeniedException {
        //Поиск пользователя в БД
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourseNotFoundException(id.toString()));

        //Проверка, текущий ли доступ запращивает доступ
        userAccessCheckService.checkUserAccess(id);

        //Формирование ответа
        String name = user.getLastName() + " " + user.getFirstName();
        String birthDate = user.getBirthDay().format(formatter);
        String workStartDate = user.getWorkStartDate().format(formatter);

        //Формирование информации о стаже работы
        String workExperienceString = formatWorkExperienceString(user.getWorkStartDate());

        //Формирование типа занятости
        String workType = "zero";
        if(user.getWorkTypeCode().equals("PART_TIME"))
            workType = PART_TIME_STRING;
        else if(user.getWorkTypeCode().equals("FULL_TIME"))
            workType = FULL_TIME_STRING;

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
        UserEntity teamLeader = userRepository
                .findById(teamLeaderId)
                .orElseThrow(
                        () -> new ResourseNotFoundException(teamLeaderId.toString())
                );

        //Проверка, существует ли уже такое лидерство
        for (LeaderShipInfo leaderShipInfo : request.getLeaderShipInfos()){

            if (Objects.equals(teamLeaderId, leaderShipInfo.getUserId()))
                throw new ResourceAlreadyExistsException(
                        "Нельзя назначать пользователя своим тимлидом"
                );

            UserEntity user = userRepository
                    .findById(leaderShipInfo.getUserId())
                    .orElseThrow(
                            () -> new ResourseNotFoundException(leaderShipInfo.getUserId().toString())
                    );

            TeamLeaderId id = new TeamLeaderId(user, teamLeader);

            //Если лидерство существует, вернуть ошибку
            if(teamLeadersRepository.existsById(id))
                throw new ResourceAlreadyExistsException(
                        "Пользователь с id " + teamLeaderId + " уже является лидером пользователя с id " + leaderShipInfo.getUserId()
                );
        }

        //Проверка, является не пытаются назначить ли пользователя лидером своему лидеру
        for (LeaderShipInfo leaderShipInfo : request.getLeaderShipInfos()){
            UserEntity user = userRepository
                    .findById(leaderShipInfo.getUserId())
                    .orElseThrow(
                            () -> new ResourseNotFoundException(leaderShipInfo.getUserId().toString())
                    );

            TeamLeaderId id = new TeamLeaderId(teamLeader, user);

            //Если лидерство существует, вернуть ошибку
            if(teamLeadersRepository.existsById(id))
                throw new ResourceAlreadyExistsException(
                        "Пользователь с id " + teamLeaderId + " является лидером пользователя с id " + leaderShipInfo.getUserId()
                );
        }

        //Формирование списка сущностей на добавление
        ArrayList<TeamLeadersEntity> teamLeadersEntityArrayList = new ArrayList<>();

        for (LeaderShipInfo leaderShipInfo : request.getLeaderShipInfos()){
            UserEntity user = userRepository
                    .findById(leaderShipInfo.getUserId())
                    .orElseThrow(
                            () -> new ResourseNotFoundException(leaderShipInfo.getUserId().toString())
                    );

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
