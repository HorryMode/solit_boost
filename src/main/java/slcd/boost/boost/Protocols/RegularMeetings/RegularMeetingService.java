package slcd.boost.boost.Protocols.RegularMeetings;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import slcd.boost.boost.General.Exceptions.OnlyTeamLeaderHaveAccessException;
import slcd.boost.boost.Protocols.Exceptions.ProtocolAlreadyHasStatusException;
import slcd.boost.boost.Protocols.Exceptions.ProtocolCannotHaveThisStatusException;
import slcd.boost.boost.Protocols.Interfaces.IProtocolRequest;
import slcd.boost.boost.Protocols.Interfaces.IProtocolService;
import slcd.boost.boost.Protocols.Entities.AttachmentEntity;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.*;
import slcd.boost.boost.Protocols.RegularMeetings.Entities.ProtocolConversationFieldEntity;
import slcd.boost.boost.Protocols.Entities.ProtocolEntity;
import slcd.boost.boost.Protocols.Entities.ProtocolStatusEntity;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EProtocolStatus;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EProtocolType;
import slcd.boost.boost.General.Exceptions.ResourceNotFoundException;
import slcd.boost.boost.General.DTOs.Attachment;
import slcd.boost.boost.General.DTOs.UUIDResponse;
import slcd.boost.boost.General.Interfaces.IResponse;
import slcd.boost.boost.Protocols.RegularMeetings.Repos.ProtocolConversationAttachmentRepository;
import slcd.boost.boost.Protocols.RegularMeetings.Repos.ProtocolConversationFieldRepository;
import slcd.boost.boost.Protocols.RegularMeetings.Repos.ProtocolRepository;
import slcd.boost.boost.Protocols.RegularMeetings.Repos.ProtocolStatusRepository;
import slcd.boost.boost.General.Constants;
import slcd.boost.boost.General.FileStorageService;
import slcd.boost.boost.General.UserAccessCheckService;
import slcd.boost.boost.Users.Entities.UserEntity;
import slcd.boost.boost.Users.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

@Service
public class RegularMeetingService implements IProtocolService {

    @Autowired
    private UserAccessCheckService userAccessCheckService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProtocolStatusRepository protocolStatusRepository;
    @Autowired
    private ProtocolRepository protocolRepository;
    @Autowired
    private ProtocolConversationFieldRepository protocolConversationFieldRepository;
    @Autowired
    private ProtocolConversationAttachmentRepository protocolConversationAttachmentRepository;
    @Autowired
    private FileStorageService fileStorageService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RegularMeetingService.class);

    @Override
    public IResponse createProtocol(@Valid IProtocolRequest protocolRequest, List<MultipartFile> files)
            throws IOException {

        CreateRegularMeetingProtocolRequest regularMeetingProtocol
                = (CreateRegularMeetingProtocolRequest) protocolRequest;

        //Список сохраняемых файлов
        HashMap<String, MultipartFile> savedFiles = new HashMap<>();

        //Поиск владельца в бд
        UserEntity owner = userService.findUserById(regularMeetingProtocol.getOwnerId());
        userAccessCheckService.checkTeamLeadAccess(owner.getId());

        //Формирование сущности протокола
        var protocolUuid = UUID.randomUUID();
        ProtocolStatusEntity protocolStatus = protocolStatusRepository.findByName(EProtocolStatus.CREATED);

        var protocolEntity = new ProtocolEntity();
        protocolEntity.setName(
                String.format(Constants.RM_PROTOCOL_NAME, LocalDate.now())
        );
        protocolEntity.setUuid(protocolUuid);
        protocolEntity.setOwner(owner);
        protocolEntity.setAssignedUser(owner);
        protocolEntity.setType(EProtocolType.REGULAR_MEETING);
        protocolEntity.setUpdated(LocalDateTime.now());
        protocolEntity.setCreated(LocalDateTime.now());
        protocolEntity.setStatus(protocolStatus);

        //Формирование списка сущностей поля протокола
        List<ProtocolConversationFieldEntity> fields= new ArrayList<>();

        for (ProtocolConversationField field : regularMeetingProtocol.getFields()){
            UserEntity responsibleUser = null;
            if(field.getResponsibleUserId() != null) {
                 responsibleUser = userService.findUserById(field.getResponsibleUserId());
                 userAccessCheckService.checkTeamLeadAccess(responsibleUser.getId());
            }

            //Формирование сущности поля протокола
            var fieldEntity = new ProtocolConversationFieldEntity();
            fieldEntity.setUuid(UUID.randomUUID());
            fieldEntity.setProtocol(protocolEntity);
            fieldEntity.setSummary(field.getSummary());
            fieldEntity.setDescription(field.getDescription());
            fieldEntity.setResponsible(responsibleUser);
            fieldEntity.setResult(field.getResult());
            fieldEntity.setStatus(field.getStatus());
            fieldEntity.setUpdated(LocalDateTime.now());

            //Формирование сущностей вложений протокола
            List<AttachmentEntity> attachmentEntities= new ArrayList<>();

            for(FilePayload attachment : field.getAttachments()){
                //Получение uuid файла
                var fileUuid = UUID.randomUUID();
                String name = attachment.parseNameWithoutExtension();
                String extension = attachment.parseExtension();

                //Проверка, существует ли файл в локальном хранилище
                protocolConversationAttachmentRepository.existsByField_Uuid(fileUuid);

                //Формирование сущности файла
                var attachmentEntity = new AttachmentEntity();
                attachmentEntity.setUuid(fileUuid);
                attachmentEntity.setField(fieldEntity);
                attachmentEntity.setName(name);
                attachmentEntity.setExtension(extension);

                //Получение файла из списка
                MultipartFile file = getAttachmentByFileNameFromRequest(attachment.getFileName(), files);

                //Проверка размера
                fileStorageService.checkFileSize(file);

                attachmentEntities.add(attachmentEntity);
                savedFiles.put(fileUuid.toString(), file);
            }

            fieldEntity.setAttachments(attachmentEntities);
            fields.add(fieldEntity);
        }
        protocolEntity.setFields(fields);

        //Сохранение сущности в БД
        protocolRepository.save(protocolEntity);

        //Сохранение файлов
        savedFiles.forEach(
                ((uuid, file) -> {
                    //Сохранение файла в локальном хранилище
                    try {
                        fileStorageService.saveFile(file, uuid);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
        );

        return new UUIDResponse(protocolUuid.toString());
    }

    @Override
    public IResponse updateProtocol(String uuid, IProtocolRequest request, List<MultipartFile> files) throws AccessDeniedException {
        try {
            @Valid
            UpdateRMProtocolRequest protocol = (UpdateRMProtocolRequest) request;

            ProtocolEntity protocolEntity = findProtocolByUuid(
                    UUID.fromString(uuid)
            );

            var ownerId = protocolEntity.getOwner().getId();
            userAccessCheckService.checkTeamLeadAccess(ownerId);

            //Проверяем, удалились ли какие либо поля
            HashMap<String, UpdateRMProtocolField> fieldsMap = new HashMap<>();
            HashMap<String, Attachment> attachmentsMap = new HashMap<>();
            HashMap<String, String> attachmentFieldMap = new HashMap<>();
            protocol.getFields().forEach(
                    field -> {
                        if (field.getUuid() == null)
                            field.setUuid(
                                    UUID.randomUUID().toString()
                            );
                        fieldsMap.put(field.getUuid(), field);

                        field.getAttachments().forEach(
                                attachment -> {
                                    if (attachment.getUuid() == null)
                                        attachment.setUuid(
                                                UUID.randomUUID().toString()
                                        );
                                    attachmentsMap.put(attachment.getUuid(), attachment);
                                    attachmentFieldMap.put(
                                            attachment.getUuid(),
                                            field.getUuid()
                                    );
                                }
                        );
                    }
            );

            protocolEntity.getFields().forEach(
                    fieldEntity -> {
                        Iterator<AttachmentEntity> iterator = fieldEntity.getAttachments().iterator();
                        while (iterator.hasNext()) {
                            AttachmentEntity attachmentEntity = iterator.next();
                            //Поиск вложения в хэшмапе, если не найдена - удаляется
                            String attachmentUuid = attachmentEntity.getUuid().toString();
                            if (!attachmentsMap.containsKey(attachmentUuid)) {
                                iterator.remove();
                                fileStorageService.fileDelete(
                                        attachmentEntity.getUuid().toString(),
                                        attachmentEntity.getExtension()
                                );
                            } else {
                                attachmentsMap.remove(attachmentUuid);
                                attachmentFieldMap.remove(attachmentUuid);
                            }
                        }
                    }
            );

            Iterator<ProtocolConversationFieldEntity> fieldIterator = protocolEntity.getFields().iterator();
            while (fieldIterator.hasNext()){
                ProtocolConversationFieldEntity fieldEntity = fieldIterator.next();
                String fieldUuid = fieldEntity.getUuid().toString();
                //Если в запросе нет поля с uuid, а в сущности есть, то удаляем поле
                if (!fieldsMap.containsKey(fieldUuid))
                    fieldIterator.remove();
                    //если поле найдено, то обновляем информацию о нём в БД
                else {
                    //Обновляем
                    UpdateRMProtocolField field = fieldsMap.get(fieldUuid);

                    fieldEntity.setSummary(field.getSummary());
                    fieldEntity.setDescription(field.getDescription());
                    fieldEntity.setResult(field.getResult());
                    fieldEntity.setUpdated(LocalDateTime.now());
                    fieldEntity.setResponsible(
                            userService.findUserById(field.getResponsibleUserId())
                    );

                    field.getAttachments().forEach(
                            attachment -> {
                                if (
                                        attachmentFieldMap.get(attachment.getUuid())
                                                .equals(field.getUuid())
                                                && attachmentsMap.
                                                containsKey(attachment.getUuid())
                                ) {
                                    var attachmentEntity = new AttachmentEntity();

                                    attachmentEntity.setUuid(
                                            UUID.fromString(attachment.getUuid())
                                    );
                                    attachmentEntity.setField(fieldEntity);
                                    attachmentEntity.setName(
                                            FilePayload.parseNameWithoutExtension(attachment.getFileName())
                                    );
                                    attachmentEntity.setExtension(
                                            FilePayload.parseExtension(attachment.getFileName())
                                    );

                                    fieldEntity.getAttachments().add(attachmentEntity);
                                }

                            }
                    );

                    fieldsMap.remove(fieldUuid);
                }
            }

            //Добавляем поля, которых нет в БД
            protocol.getFields().forEach(
                    field -> {
                        if (fieldsMap.containsKey(field.getUuid())) {

                            var fieldEntity = new ProtocolConversationFieldEntity();

                            fieldEntity.setUuid(
                                    UUID.fromString(field.getUuid()
                                    ));

                            fieldEntity.setProtocol(protocolEntity);
                            fieldEntity.setSummary(field.getSummary());
                            fieldEntity.setDescription(field.getDescription());
                            fieldEntity.setResponsible(
                                    userService.findUserById(field.getResponsibleUserId())
                            );
                            fieldEntity.setStatus(field.getStatus());
                            fieldEntity.setResult(field.getResult());
                            fieldEntity.setUpdated(LocalDateTime.now());

                            field.getAttachments().forEach(
                                    attachment -> {
                                        if (attachmentsMap.containsKey(attachment.getUuid())) {
                                            var attachmentEntity = new AttachmentEntity();

                                            attachmentEntity.setUuid(
                                                    UUID.fromString(attachment.getUuid())
                                            );
                                            attachmentEntity.setField(fieldEntity);
                                            attachmentEntity.setName(
                                                    FilePayload.parseNameWithoutExtension(attachment.getFileName())
                                            );
                                            attachmentEntity.setExtension(
                                                    FilePayload.parseExtension(attachment.getFileName())
                                            );

                                            fieldEntity.getAttachments().add(attachmentEntity);
                                        }
                                    }
                            );

                            protocolEntity.getFields().add(fieldEntity);
                        }
                    }
            );

            protocolRepository.save(protocolEntity);
            return new UUIDResponse(protocolEntity.getUuid().toString());
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE.getResourceBundleName(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public IResponse getProtocol(String uuid) throws AccessDeniedException {
        //Поиск протокола в БД
        var protocolEntity =  findProtocolByUuid(
                UUID.fromString(uuid)
        );

        //Проверка, есть ли доступ у пользователя к файлу
        var ownerId = protocolEntity.getOwner().getId();
        userAccessCheckService.checkTeamLeadAccess(ownerId);

        //Формирование ответа
        var response = new ProtocolResponse();
        response.setUuid(uuid);
        response.setName(protocolEntity.getName());
        if(protocolEntity.getStatus() != null)
            response.setStatus(protocolEntity.getStatus().getName().toString());
        response.setCreated(protocolEntity.getCreated());
        response.setUpdated(protocolEntity.getUpdated());

        //Формирование списка полей
        List<ConversationField> fields = new ArrayList<>();
        for(ProtocolConversationFieldEntity fieldEntity: protocolEntity.getFields()){

            var field = new ConversationField();

            field.setUuid(fieldEntity.getUuid().toString());
            field.setSummary(fieldEntity.getSummary());
            field.setDescription(fieldEntity.getDescription());

            UserShort userShort = userService.findShortUserInfoById(fieldEntity.getResponsible().getId());
            field.setResponsibleUser(userShort);

            field.setResult(fieldEntity.getResult());
            field.setStatus(fieldEntity.getStatus());

            //Формирование списка вложений поля
            List<Attachment> attachments = new ArrayList<>();
            for(AttachmentEntity attachmentEntity: fieldEntity.getAttachments()){
                var attachment = new Attachment();

                attachment.setUuid(attachmentEntity.getUuid().toString());
                String name = attachmentEntity.getName().concat(".").concat(attachmentEntity.getExtension());
                attachment.setFileName(name);

                attachments.add(attachment);
            }
            field.setAttachments(attachments);

            fields.add(field);
        }
        response.setFields(fields);

        return response;
    }

    @Override
    public ProtocolPageResponse getProtocols(Long ownerId, Pageable pageable) {
        UserEntity owner = userService.findUserById(ownerId);

        return ProtocolPageResponse.mapPageEntity(
                protocolRepository.findByOwner(owner, pageable)
        );
    }

    @Override
    public IResponse getAttachment(String attachmentUuid) throws IOException {
        //Получение информации о вложении из бд
        var attachment
                = protocolConversationAttachmentRepository.findByUuid(UUID.fromString(attachmentUuid))
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Constants.RM_ATTACHMENT_NOT_FOUND_MESSAGE, attachmentUuid)
                ));

        //Проверка, есть ли доступ у пользователя к файлу
        var ownerId = attachment.getField()
                .getProtocol()
                .getOwner().getId();
        userAccessCheckService.checkTeamLeadAccess(ownerId);

        //Получение файла из локального хранилища
        File file = fileStorageService
                .getFileContent(attachmentUuid, attachment.getExtension());

        //Формирование ответа
        var fileDownloadPayload = new FileDownloadPayload();
        String fileName = attachment.getName() + "." + attachment.getExtension();
        fileDownloadPayload.setFileName(fileName);
        fileDownloadPayload.setFile(new UrlResource(file.toURI()));

        return fileDownloadPayload;
    }

    @Override
    public void setStatusOnApproval(String uuid) throws AccessDeniedException {
        ProtocolEntity protocolEntity = findProtocolByUuid(
                UUID.fromString(uuid)
        );

        userAccessCheckService.checkTeamLeadAccess(protocolEntity.getOwner().getId());

        //Поиск статусов
        ProtocolStatusEntity status = protocolStatusRepository.findByName(EProtocolStatus.ON_APPROVAL);
        ProtocolStatusEntity approvedStatus = protocolStatusRepository.findByName(EProtocolStatus.APPROVED);

        if(protocolEntity.getStatus().equals(status))
            throw new ProtocolAlreadyHasStatusException(
                    EProtocolStatus.ON_APPROVAL.toString(),
                    protocolEntity.getUuid().toString()
            );
        else if(protocolEntity.getStatus().equals(approvedStatus)){
            throw new ProtocolCannotHaveThisStatusException(
                    EProtocolStatus.ON_APPROVAL.toString(),
                    protocolEntity.getUuid().toString(),
                    EProtocolStatus.APPROVED.toString()
            );
        }
        else{
            protocolEntity.setStatus(status);
            protocolRepository.save(protocolEntity);
        }
    }

    @Override
    public void setStatusCreated(String uuid) throws AccessDeniedException {
        ProtocolEntity protocolEntity = findProtocolByUuid(
                UUID.fromString(uuid)
        );

        userAccessCheckService.checkTeamLeadAccess(protocolEntity.getOwner().getId());

        //Поиск статусов
        ProtocolStatusEntity status = protocolStatusRepository.findByName(EProtocolStatus.CREATED);

        if(protocolEntity.getStatus().equals(status))
            throw new ProtocolAlreadyHasStatusException(
                    EProtocolStatus.CREATED.toString(),
                    protocolEntity.getUuid().toString()
            );
        else{
            protocolEntity.setStatus(status);
            protocolRepository.save(protocolEntity);
        }
    }

    @Override
    public void setStatusApproved(String uuid) throws AccessDeniedException {
        ProtocolEntity protocolEntity = findProtocolByUuid(
                UUID.fromString(uuid)
        );

        Long userId = protocolEntity.getOwner().getId();

        userAccessCheckService.checkTeamLeadAccess(userId);

        if(!(userAccessCheckService.isTeamLeader()
                || userAccessCheckService.isSubdivisionHead(userId)
                || userAccessCheckService.isAdmin()
        )) throw new OnlyTeamLeaderHaveAccessException(
                Constants.ONLY_TEAM_LEADER_HAVE_ACCESS_MESSAGE
        );


        //Поиск статусов
        ProtocolStatusEntity status = protocolStatusRepository.findByName(EProtocolStatus.APPROVED);

        if(protocolEntity.getStatus().equals(status))
            throw new ProtocolAlreadyHasStatusException(
                    EProtocolStatus.APPROVED.toString(),
                    protocolEntity.getUuid().toString()
            );
        else{
            protocolEntity.setStatus(status);
            protocolRepository.save(protocolEntity);
        }
    }

    public ProtocolEntity findProtocolByUuid(UUID uuid){
        return protocolRepository.findByUuidAndType(uuid, EProtocolType.REGULAR_MEETING)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Constants.RM_PROTOCOL_NOT_FOUND_MESSAGE, uuid)
                ));
    }

    private MultipartFile getAttachmentByFileNameFromRequest(String fileName,List<MultipartFile> files){
        for(MultipartFile file : files){
            if(fileName.equals(file.getOriginalFilename()))
                return  file;
        }
        throw new ResourceNotFoundException("");
    }
}
