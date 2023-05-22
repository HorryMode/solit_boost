package slcd.boost.boost.Syncs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import slcd.boost.boost.Auths.AesService;
import slcd.boost.boost.Protocols.Entities.ProtocolStatusEntity;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EProtocolStatus;
import slcd.boost.boost.Protocols.RegularMeetings.Repos.ProtocolStatusRepository;
import slcd.boost.boost.Users.ERole;
import slcd.boost.boost.Users.Entities.RoleEntity;
import slcd.boost.boost.Users.Repos.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProtocolStatusRepository protocolStatusRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AesService aesService;

    @Autowired
    private SyncService syncService;

    @Override
    public void run(String... args) throws Exception {

        if(roleRepository.count() == 0){
            for(ERole role : ERole.values()){
                var roleEntity = new RoleEntity();
                roleEntity.setName(role);
                roleRepository.save(roleEntity);
            }
        }

        if(protocolStatusRepository.count() == 0){
            for (EProtocolStatus name : EProtocolStatus.values()){
                var statusEntity = new ProtocolStatusEntity();
                statusEntity.setName(name);
                protocolStatusRepository.save(statusEntity);
            }
        }

        System.out.println(aesService.encrypt(
                "{\"username\": \"dkoshkin\", \"password\":\"KGf9QS99QS\"}"
        ));
    }
}
