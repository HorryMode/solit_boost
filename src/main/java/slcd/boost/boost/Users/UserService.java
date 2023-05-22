package slcd.boost.boost.Users;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Auths.DTOs.LdapInfoDTO;
import slcd.boost.boost.Auths.DTOs.ProfileResponse;
import slcd.boost.boost.Auths.LdapService;
import slcd.boost.boost.Auths.Repos.AdUserInfoEntityRepository;
import slcd.boost.boost.General.Entities.PostEntity;
import slcd.boost.boost.General.Entities.SubdivisionEntity;
import slcd.boost.boost.General.Interfaces.IResponse;
import slcd.boost.boost.General.Exceptions.ResourceAlreadyExistsException;
import slcd.boost.boost.General.Exceptions.ResourceNotFoundException;
import slcd.boost.boost.General.DTOs.SearchCriteria;
import slcd.boost.boost.General.Interfaces.ISearchRequest;
import slcd.boost.boost.General.Repos.PostRepository;
import slcd.boost.boost.General.Repos.SubdivisionRepository;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.UserShort;
import slcd.boost.boost.Syncs.DTOs.InternalUserInfo;
import slcd.boost.boost.Syncs.DTOs.UserProductInfo;
import slcd.boost.boost.Users.DTOs.*;
import slcd.boost.boost.General.DTOs.MessageResponse;
import slcd.boost.boost.Users.Entities.*;
import slcd.boost.boost.Users.Repos.ProductRepository;
import slcd.boost.boost.Users.Repos.RoleRepository;
import slcd.boost.boost.Users.Repos.TeamLeadersRepository;
import slcd.boost.boost.Users.Repos.UserRepository;
import slcd.boost.boost.General.Constants;
import slcd.boost.boost.General.UserAccessCheckService;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UserService {

    //Установка формата даты
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAccessCheckService userAccessCheckService;

    @Autowired
    private TeamLeadersRepository teamLeadersRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SubdivisionRepository subdivisionRepository;
    @Autowired
    private PostRepository postEntityRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private LdapService ldapService;
    @Autowired
    private UserProductRepository userProductRepository;
    @Autowired
    private ProductRepository productRepository;

    public ShortUserInfoResponse getShortUserInfo(Long id) throws AccessDeniedException {
        //Поиск пользователя в бд
        UserEntity user = findUserById(id);

        //Проверка, текущий ли пользователь запращивает доступ
        userAccessCheckService.checkTeamLeadAccess(id);

        //Формирование ответа
        String name = user.getLastname() + " " + user.getFirstname();
        return new ShortUserInfoResponse(name, null);
    }

    public UserInfoResponse getUserInfo(Long id) throws AccessDeniedException {
        //Поиск пользователя в БД
        UserEntity user = findUserById(id);

        //Проверка, текущий ли доступ запращивает доступ
        userAccessCheckService.checkTeamLeadAccess(id);

        //Формирование ответа
        String name = user.getLastname() + " " + user.getFirstname();
        String birthDate = user.getBirthDay().format(formatter);
        String workStartDate = user.getWorkStartDate().format(formatter);

        //Формирование информации о стаже работы
        String workExperienceString = formatWorkExperienceString(user.getWorkStartDate());

        //Формирование типа занятости
        String workType = "zero";
        if(user.getRate().equals("PART_TIME"))
            workType = Constants.PART_TIME_STRING;
        else if(user.getRate().equals("FULL_TIME"))
            workType = Constants.FULL_TIME_STRING;

        return new UserInfoResponse(
                name,
                birthDate,
                user.getEmail(),
                workStartDate,
                workExperienceString,
                user.getPost().getName(),
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Constants.USER_NOT_FOUND_MESSAGE, id)
                ));
    }

    public List<UserEntity> findAllNotArchived(){
        return userRepository.findByArchived(false);
    }

    public UserEntity findUserByUUID(UUID uuid){

        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Constants.USER_NOT_FOUND_MESSAGE, uuid)
                ));
    }

    public List<UserProductResponse> findUserProduct(Long id){
        UserEntity user = findUserById(id);

        return user
                .getUserProducts()
                .stream()
                .map(UserProductResponse::mapFromEntity)
                .toList();
    }

    public UserEntity findUserByUsername(String username){

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Constants.USER_NOT_FOUND_MESSAGE, username)
                ));
    }

    public UserShort findShortUserInfoById(Long id){
        UserEntity user = findUserById(id);
        String userName = user.getLastname().concat(" ").concat(user.getFirstname());

        return new UserShort(id, userName);
    }

    public List<IResponse> findUsers(UsersSearchRequest searchRequest) throws AccessDeniedException {

        userAccessCheckService.checkAccess();

        List<SearchCriteria> params = new ArrayList<>();
        String currentUserId = String.valueOf(userAccessCheckService.getUserIdFromJwtToken());
        if(userAccessCheckService.isAdmin()) {

        } else if (userAccessCheckService.isSubdivisionHead()){
            String subdivisionId
                    = findUserById(Long.valueOf(currentUserId))
                    .getSubdivision()
                    .getId()
                    .toString();
            params.add(new SearchCriteria("subdivision", ":", subdivisionId));
        } else if(userAccessCheckService.isTeamLeader()) {
            params.add(new SearchCriteria("teamLeader", ":", currentUserId));
        }
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

        Comparator<AllUsersResponse> comparator;
        if(Objects.isNull(searchRequest.getSortBy()) || Objects.isNull(searchRequest.getSortBy().getSortField()))
            comparator = Comparator.comparing(AllUsersResponse::getFullName);
        else{
            switch (searchRequest.getSortBy().getSortField()) {
                case "startWorkDate" -> comparator = Comparator.comparing(AllUsersResponse::getStartWorkDate);
                case "workMail" -> comparator = Comparator.comparing(AllUsersResponse::getWorkMail);
                default -> comparator = Comparator.comparing(AllUsersResponse::getFullName);
            }
            if(Objects.equals(searchRequest.getSortBy().getSortType(), "DESK"))
                comparator = comparator.reversed();
        }

        //Формирование списка ответа
        return userEntities.stream()
                .map(userEntity -> new AllUsersResponse(
                        userEntity.getId(),
                        userEntity.getFullName(),
                        userEntity.getWorkStartDate(),
                        userEntity.getEmail()
                ))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<?> findUsersByProducts(ISearchRequest iSearchRequest){

        ProductsSearchRequest searchRequest = (ProductsSearchRequest) iSearchRequest;

        //Получение фильтра по продуктам
        Predicate<ProductUsersResponse> productPredicate = productUsers -> true;
        if(
                searchRequest.getProducts() != null
                && !searchRequest.getProducts().isEmpty()
        ){
            HashSet<String> products = new HashSet<>(searchRequest.getProducts());
            productPredicate = productUsers -> products.contains(productUsers.getName());
        }

        //Сортировка по названию продукта
        Comparator<ProductUsersResponse> comparator = Comparator.comparing(ProductUsersResponse::getName);

        Predicate<UserProductEntity> predicateUserProduct = userProduct -> false;
        if(userAccessCheckService.isAdmin())
            predicateUserProduct = userProduct -> true;

        else if(userAccessCheckService.isSubdivisionHead()) {
            SubdivisionEntity subdivision = findUserById(
                    userAccessCheckService.getUserIdFromJwtToken()
            ).getSubdivision();

            predicateUserProduct
                    = userProduct -> userProduct.getId().getUser().getSubdivision().equals(subdivision);

        } else if(userAccessCheckService.isTeamLeader()) {
            UserEntity teamLeader = findUserById(
                    userAccessCheckService.getUserIdFromJwtToken()
            );

            predicateUserProduct = userProduct -> userAccessCheckService.isTeamLeader(
                    userProduct.getId().getUser(),
                    teamLeader
            );
        }

        Predicate<UserProductEntity> finalPredicate = predicateUserProduct;
        return productRepository
                .findAll()
                .stream()
                .map(product -> ProductUsersResponse.mapFromEntity(product, finalPredicate))
                .filter(productUsersResponse -> !productUsersResponse.getUsers().isEmpty())
                .filter(productPredicate)
                .sorted(comparator)
                .toList();
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

    public boolean saveInternalUser(InternalUserInfo internalUserInfo) {
        try {
            UserEntity user = new UserEntity();

            LdapInfoDTO ldapInfoDTO = ldapService.findByEmail(internalUserInfo.getEmail());

            //Заполнение роли
            Set<RoleEntity> roles =  new HashSet<>();
            roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow());
            user.setRoles(roles);

            //Заполнение имени пользователя и uuid
            user.setUsername(ldapInfoDTO.getUsername());
            user.setUuid(
                    UUID.fromString(internalUserInfo.getId())
            );

            //Заполнение ФИО
            user.setFullName(internalUserInfo.getName());
            user.setFirstname(internalUserInfo.getFirstName());
            user.setLastname(internalUserInfo.getLastName());
            user.setMiddlename(internalUserInfo.getMiddleName());

            //Заполнение дат
            user.setBirthDay(
                    LocalDate.parse(internalUserInfo.getBirthDate())
            );
            if(!Objects.isNull(internalUserInfo.getDateStart()))
                user.setWorkStartDate(
                    LocalDate.parse(internalUserInfo.getDateStart())
                );
            else
                user.setWorkStartDate(null);
            user.setRegistered(LocalDateTime.now());
            user.setUpdated(LocalDateTime.now());

            //Заполнение должности пола
            if(internalUserInfo.getSex().equals("m"))
                user.setGenderCode("MALE");
            else if(internalUserInfo.getSex().equals("f"))
                user.setGenderCode("FEMALE");

            //Заполнение отдела и должности
            SubdivisionEntity subdivision = subdivisionRepository.findByUuid(
                    UUID.fromString(internalUserInfo.getSubdivision())
            ).orElseThrow();
            user.setSubdivision(subdivision);
            PostEntity post = postEntityRepository.findByUuid(
                    UUID.fromString(internalUserInfo.getPost())
            ).orElseThrow();
            user.setPost(post);

            //Заполнение параметров почта, тип занятости, локация и признака архивности
            user.setEmail(internalUserInfo.getEmail());
            user.setRate(internalUserInfo.getRate());
            user.setLocation(internalUserInfo.getLocation());
            user.setArchived(false);

            //Сохранение сущности в таблице f_users
            user = userRepository.save(user);

            for(UserProductInfo product : internalUserInfo.getProducts()){
                ProductEntity productEntity= productService.findByUuid(
                        UUID.fromString(product.getProduct().getId())
                );

                UserProductId id = new UserProductId(user, productEntity);
                UserProductEntity userProductEntity = new UserProductEntity();

                userProductEntity.setId(id);
                userProductEntity.setRole(product.getRole());
                userProductEntity.setBusy(product.getBusy());

                userProductRepository.save(userProductEntity);
            }

            //Формирование и сохранение сущности информации о пользователе в ActiveDirectory
            ldapService.saveAdUserInfo(ldapInfoDTO, user);

            return true;
        } catch (NoSuchElementException | PersistenceException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByUuid(UUID uuid){
        return userRepository.existsByUuid(uuid);
    }

    public boolean existsByUuidAndArchived(UUID uuid){
        return userRepository.existsByUuidAndArchived(uuid, true);
    }

    public void setArchivedFalse(UUID uuid) {
        UserEntity user = findUserByUUID(uuid);
        user.setArchived(false);
        userRepository.save(user);
    }

    public void setArchivedTrue(UUID uuid) {
        UserEntity user = findUserByUUID(uuid);
        user.setArchived(true);
        userRepository.save(user);
    }

    public void compareWithInternalInfo(InternalUserInfo internalUserInfo) {
        UserEntity user = findUserByUUID(
                UUID.fromString(internalUserInfo.getId())
        );

        //Проверка ФИО
        if(!Objects.equals(
                user.getFullName(), internalUserInfo.getName()
        )) user.setFullName(internalUserInfo.getName());
        if(!Objects.equals(
                user.getFirstname(), internalUserInfo.getFirstName()
        )) user.setFirstname(internalUserInfo.getFirstName());
        if(!Objects.equals(
                user.getLastname(), internalUserInfo.getLastName()
        )) user.setLastname(internalUserInfo.getLastName());
        if(!Objects.equals(
                user.getMiddlename(), internalUserInfo.getMiddleName()
        )) user.setMiddlename(internalUserInfo.getMiddleName());

        //Проверка дат
        LocalDate birthDay = LocalDate.parse(internalUserInfo.getBirthDate());
        if(!Objects.equals(
                user.getBirthDay(), birthDay
        )) user.setBirthDay(birthDay);
        LocalDate workStartDate;
        if(Objects.isNull(internalUserInfo.getDateStart()))
            workStartDate = null;
        else
            workStartDate = LocalDate.parse(internalUserInfo.getDateStart());
        if(!Objects.equals(
                user.getWorkStartDate(), workStartDate
        )) user.setWorkStartDate(workStartDate);

        //Проверка пола
        if(Objects.equals(user.getGenderCode(), "MALE")
                && Objects.equals(internalUserInfo.getSex(), "f"))
            user.setGenderCode("FEMALE");
        else if(Objects.equals(user.getGenderCode(), "FEMALE")
                && Objects.equals(internalUserInfo.getSex(), "m"))
            user.setGenderCode("MALE");

        //Проверка должности и отдела
        UUID postUUID = UUID.fromString(internalUserInfo.getPost());
        if(!Objects.equals(
                user.getPost().getUuid(),
                postUUID
        )) user.setPost(
                postEntityRepository.findByUuid(postUUID).orElseThrow()
        );
        UUID subdivisionUUID = UUID.fromString(internalUserInfo.getSubdivision());
        if(!Objects.equals(
                user.getSubdivision().getUuid(),
                subdivisionUUID
        )) user.setSubdivision(
                subdivisionRepository.findByUuid(subdivisionUUID).orElseThrow()
        );

        //Проверка типа занятости и локации
        if(!Objects.equals(
                user.getRate(), internalUserInfo.getRate()
        )) user.setRate(internalUserInfo.getRate());
        if(!Objects.equals(
                user.getLocation(), internalUserInfo.getLocation()
        )) user.setLocation(internalUserInfo.getLocation());

        //Проверка команд сотрудника
        HashMap<String, UserProductInfo> uuidsSet = new HashMap<>();
        internalUserInfo.getProducts().forEach(
                userProductInfo -> {
                    uuidsSet.put(userProductInfo.getProduct().getId(), userProductInfo);
                }
        );

        List<UserProductEntity> userProducts = userProductRepository.findById_User(user);
        userProducts.forEach(
                userProductEntity -> {

                    String uuid = userProductEntity.getId().getProduct().getUuid().toString();
                    //Проверка, удалена ли связь с компандой
                    if(!uuidsSet.containsKey(uuid))
                        userProductRepository.delete(userProductEntity);

                    //Если пользователь не удалён, тогда проверка изменились ли параметра занятости
                    else {
                        UserProductInfo userProductInfo = uuidsSet.get(uuid);
                        UserProductEntity editedUserProductEntity = userProductRepository.findById(
                                userProductEntity.getId()
                        ).orElseThrow();

                        //Сравнение занятости
                        if(!Objects.equals(
                                editedUserProductEntity.getBusy(),
                                userProductInfo.getBusy()
                        )) editedUserProductEntity.setBusy(userProductInfo.getBusy());

                        //Сравнение роли
                        if(!Objects.equals(
                                editedUserProductEntity.getRole(),
                                userProductInfo.getRole()
                        )) editedUserProductEntity.setBusy(userProductInfo.getBusy());

                        //Если объект изменился, сохраняем его
                        if(!Objects.equals(
                                editedUserProductEntity,
                                userProductEntity
                        )) userProductRepository.save(editedUserProductEntity);
                        uuidsSet.remove(uuid);
                    }
                }
        );

        //Для каждой оставшейся команды в хэшсете выполняем создание и сохранение связи пользователя с командой
        uuidsSet.forEach(
                (uuid, userProductInfo) -> {
                    ProductEntity productEntity = productService.findByUuid(
                            UUID.fromString(uuid)
                    );

                    UserProductId userProductId = new UserProductId(user, productEntity);
                    UserProductEntity userProductEntity = new UserProductEntity();

                    userProductEntity.setId(userProductId);
                    userProductEntity.setBusy(userProductInfo.getBusy());
                    userProductEntity.setRole(userProductInfo.getRole());

                    userProductRepository.save(userProductEntity);
                }
        );

        if(!user.equals(
                findUserByUUID(UUID.fromString(internalUserInfo.getId()))
        )){
            user.setUpdated(LocalDateTime.now());
            UserEntity newUser = userRepository.save(user);
        }
    }

    public List<ProductShortResponse> findAvailable(){
        if(userAccessCheckService.isAdmin() || userAccessCheckService.isSubdivisionHead())
            return productRepository
                    .findAll()
                    .stream()
                    .map(ProductShortResponse::mapFromEntity)
                    .toList();

        else {
            List<ProductShortResponse> products = new ArrayList<>();
            UserEntity user = findUserById(
                    userAccessCheckService.getUserIdFromJwtToken()
            );
            user.getUserProducts().forEach(
                    userProductEntity -> {
                        ProductEntity productEntity = userProductEntity.getProduct();
                        products.add(
                                ProductShortResponse.mapFromEntity(productEntity)
                        );
                    }
            );
            return products;
        }
    }

    public ProfileResponse findShortUserInfoById() {
        UserEntity userEntity = findUserById(
                userAccessCheckService.getUserIdFromJwtToken()
        );

        return ProfileResponse.mapFromEntity(userEntity);
    }
}
