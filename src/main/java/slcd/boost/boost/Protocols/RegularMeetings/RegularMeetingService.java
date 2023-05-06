package slcd.boost.boost.Protocols.RegularMeetings;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import slcd.boost.boost.Protocols.Interfaces.IProtocolRequest;
import slcd.boost.boost.Protocols.Interfaces.IProtocolService;
import slcd.boost.boost.General.Interfaces.ISearchRequest;
import slcd.boost.boost.Protocols.Entities.ProtocolConversationAttachmentEntity;
import slcd.boost.boost.Protocols.RegularMeetings.Entities.ProtocolConversationFieldEntity;
import slcd.boost.boost.Protocols.Entities.ProtocolEntity;
import slcd.boost.boost.Protocols.Entities.ProtocolStatusEntity;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EConversationFieldStatus;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EProtocolStatus;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EProtocolType;
import slcd.boost.boost.General.Exceptions.ResourseNotFoundException;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.FileDownloadPayload;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.FilePayload;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.ProtocolConversationField;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.UserShort;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.CreateRegularMeetingProtocolRequest;
import slcd.boost.boost.General.DTOs.Attachment;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.ConversationField;
import slcd.boost.boost.General.DTOs.UUIDResponse;
import slcd.boost.boost.Protocols.RegularMeetings.DTOs.ProtocolResponse;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    @Override
    public IResponse createProtocol(@Valid IProtocolRequest protocolRequest, List<MultipartFile> files)
            throws IOException {

        CreateRegularMeetingProtocolRequest regularMeetingProtocol
                = (CreateRegularMeetingProtocolRequest) protocolRequest;

        //Список сохраняемых файлов
        List<MultipartFile> savedFiles = new ArrayList<>();

        //Поиск владельца в бд
        UserEntity owner = userService.findUserById(regularMeetingProtocol.getOwnerId());
        userAccessCheckService.checkTeamLeadAccess(owner.getId());

        //Формирование сущности протокола
        var protocolUuid = UUID.randomUUID();
        ProtocolStatusEntity protocolStatus = protocolStatusRepository.findByName(EProtocolStatus.CREATED);

        var protocolEntity = new ProtocolEntity();
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
            }

            //Формирование сущности поля протокола
            var fieldEntity = new ProtocolConversationFieldEntity();
            fieldEntity.setUuid(UUID.randomUUID());
            fieldEntity.setProtocol(protocolEntity);
            fieldEntity.setSummary(field.getSummary());
            fieldEntity.setDescription(field.getDescription());
            fieldEntity.setResponsible(responsibleUser);
            fieldEntity.setResult(field.getResult());
            fieldEntity.setStatus(EConversationFieldStatus.IN_PROGRESS);
            fieldEntity.setUpdated(LocalDateTime.now());

            //Формирование сущностей вложений протокола
            List<ProtocolConversationAttachmentEntity> attachmentEntities= new ArrayList<>();

            for(FilePayload attachment : field.getAttachments()){
                //Получение uuid файла
                var fileUuid = UUID.randomUUID();
                String name = attachment.parseNameWithoutExtension();
                String extension = attachment.parseExtension();

                //Проверка, существует ли файл в локальном хранилище
                protocolConversationAttachmentRepository.existsByField_Uuid(fileUuid);

                //Формирование сущности файла
                var attachmentEntity = new ProtocolConversationAttachmentEntity();
                attachmentEntity.setUuid(fileUuid);
                attachmentEntity.setField(fieldEntity);
                attachmentEntity.setName(name);
                attachmentEntity.setExtension(extension);

                //Получение файла из списка
                MultipartFile file = getAttachmentByFileNameFromRequest(attachment.getFileName(), files);

                //Проверка размера
                fileStorageService.checkFileSize(file);

                attachmentEntities.add(attachmentEntity);
                savedFiles.add(file);
            }

            fieldEntity.setAttachments(attachmentEntities);
            fields.add(fieldEntity);
        }
        protocolEntity.setFields(fields);

        //Сохранение сущности в БД
        protocolRepository.save(protocolEntity);

        //Сохранение файлов
        for(MultipartFile file : savedFiles){
            String extension = Objects.requireNonNull(file.getOriginalFilename());

            //Сохранение файла в локальном хранилище
            fileStorageService.saveFile(file,
                    FilePayload.parseExtension(extension),
                    Constants.SAVE_FILE_DIRECTORY);
        }

        return new UUIDResponse(protocolUuid.toString());
    }

    @Override
    public IResponse updateProtocol(IProtocolRequest protocol, List<MultipartFile> files) {
        return null;
    }

    @Override
    public IResponse getProtocol(String uuid) throws AccessDeniedException {
        //Поиск протокола в БД
        var protocolEntity
                = protocolRepository.findByUuidAndType(UUID.fromString(uuid), EProtocolType.REGULAR_MEETING)
                .orElseThrow(() -> new ResourseNotFoundException(
                        String.format(Constants.RM_PROTOCOL_NOT_FOUND_MESSAGE, uuid)
                ));

        //Проверка, есть ли доступ у пользователя к файлу
        var ownerId = protocolEntity.getOwner().getId();
        userAccessCheckService.checkTeamLeadAccess(ownerId);

        //Формирование ответа
        var response = new ProtocolResponse();
        response.setUuid(uuid);
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
            field.setStatus(fieldEntity.getStatus().toString());

            //Формирование списка вложений поля
            List<Attachment> attachments = new ArrayList<>();
            for(ProtocolConversationAttachmentEntity attachmentEntity: fieldEntity.getAttachments()){
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
    public List<IResponse> getProtocols(ISearchRequest searchRequest) {
        return null;
    }

    @Override
    public IResponse getAttachment(String attachmentUuid) throws IOException {
        //Получение информации о вложении из бд
        var attachment
                = protocolConversationAttachmentRepository.findByUuid(UUID.fromString(attachmentUuid))
                .orElseThrow(() -> new ResourseNotFoundException(
                        String.format(Constants.RM_ATTACHMENT_NOT_FOUND_MESSAGE, attachmentUuid)
                ));

        //Проверка, есть ли доступ у пользователя к файлу
        var ownerId = attachment.getField()
                .getProtocol()
                .getOwner().getId();
        userAccessCheckService.checkTeamLeadAccess(ownerId);

        //Получение файла из локального хранилища
        File file = fileStorageService
                .getFileContent(attachmentUuid, attachment.getExtension(),Constants.SAVE_FILE_DIRECTORY);

        //Формирование ответа
        var fileDownloadPayload = new FileDownloadPayload();
        String fileName = attachment.getName() + "." + attachment.getExtension();
        fileDownloadPayload.setFileName(fileName);
        fileDownloadPayload.setFile(new UrlResource(file.toURI()));

        return fileDownloadPayload;
    }

    @Override
    public void setStatusOnApproval(String uuid) {

    }

    @Override
    public void setStatusCreated(String uuid) {

    }

    @Override
    public void setStatusApproved(String uuid) {

    }

    private MultipartFile getAttachmentByFileNameFromRequest(String fileName,List<MultipartFile> files){
        for(MultipartFile file : files){
            if(fileName.equals(file.getOriginalFilename()))
                return  file;
        }
        throw new ResourseNotFoundException("");
    }
}
