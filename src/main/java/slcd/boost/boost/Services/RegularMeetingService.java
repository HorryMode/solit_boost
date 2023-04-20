package slcd.boost.boost.Services;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Models.*;
import slcd.boost.boost.Models.Enum.EConversationFieldStatus;
import slcd.boost.boost.Models.Enum.EProtocolStatus;
import slcd.boost.boost.Models.Enum.EProtocolType;
import slcd.boost.boost.Models.Exceptions.ResourseNotFoundException;
import slcd.boost.boost.Payloads.General.RegularMeeting.ProtocolConversationAttachment;
import slcd.boost.boost.Payloads.General.RegularMeeting.ProtocolConversationField;
import slcd.boost.boost.Payloads.Requests.RegularMeeting.CreateRegularMeetingProtocolRequest;
import slcd.boost.boost.Payloads.Responses.General.UUIDResponse;
import slcd.boost.boost.Payloads.Responses.RegularMeeting.ProtocolResponse;
import slcd.boost.boost.Repositories.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegularMeetingService {

    @Autowired
    private UserAccessCheckService userAccessCheckService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProtocolStatusRepository protocolStatusRepository;

    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    private ProtocolConversationFieldRepository protocolConversationFieldRepository;

    @Autowired
    private ProtocolConversationAttachmentRepository protocolConversationAttachmentRepository;

    public UUIDResponse createProtocol(@Valid CreateRegularMeetingProtocolRequest protocol)
            throws IOException {

        //Поиск владельца в бд
        UserEntity owner = userRepository.findById(protocol.getOwnerId())
                .orElseThrow(() -> new ResourseNotFoundException(protocol.getOwnerId().toString()));

        userAccessCheckService.checkTeamLeadAccess(owner.getId());

        //Формирование сущности протокола
        var protocolUuid = UUID.randomUUID();
        ProtocolStatusEntity protocolStatus = protocolStatusRepository.findByName(EProtocolStatus.CREATED);

        var protocolEntity = new ProtocolEntity(
                protocolUuid,
                owner,
                owner,
                EProtocolType.REGULAR_MEETING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                protocolStatus
        );
        protocolEntity = protocolRepository.save(protocolEntity);

        //Формирование сущности поля протокола
        for (ProtocolConversationField field : protocol.getFields()){
            UserEntity responsibleUser = null;
            if(field.getResponsibleUserId() != null) {
                 responsibleUser = userRepository.findById(field.getResponsibleUserId())
                        .orElseThrow(() -> new ResourseNotFoundException(field.getResponsibleUserId().toString()));
            }

            var fieldEntity = new ProtocolConversationFieldEntity(
                    protocolEntity,
                    field.getSummary(),
                    field.getDescription(),
                    responsibleUser,
                    field.getResult(),
                    EConversationFieldStatus.IN_PROGRESS,
                    LocalDateTime.now()
            );
            //Сохранение сущности поля
            var returnedFieldEntity = protocolConversationFieldRepository.save(fieldEntity);

            //Формирование сущностей вложений протокола
            if(field.getAttachments() != null)
                for(ProtocolConversationAttachment attachment : field.getAttachments()){
                    File file = attachment.getAttachment();


                    var attachmentEntity = new ProtocolConversationAttachmentEntity(
                            fieldEntity,
                            Files.readAllBytes(file.toPath())
                    );

                    protocolConversationAttachmentRepository.save(attachmentEntity);
                }
        }

        return new UUIDResponse(protocolUuid.toString());
    }

    public ProtocolResponse getProtocolByUUID(@Valid String uuid){
        //Поиск протокола в БД
        var protocolEntity
                = protocolRepository.findByUuidAndType(UUID.fromString(uuid), EProtocolType.REGULAR_MEETING)
                .orElseThrow(() -> new ResourseNotFoundException(uuid));

        //формирование ответа
        ProtocolResponse response = new ProtocolResponse();
        response.setUuid(uuid);
        if(protocolEntity.getStatus() != null)
            response.setStatus(protocolEntity.getStatus().getId().toString());
        response.setCreated(protocolEntity.getCreated());
        response.setUpdated(protocolEntity.getUpdated());

        return response;
    }
}
