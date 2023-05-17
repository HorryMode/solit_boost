package slcd.boost.boost.Syncs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import slcd.boost.boost.Protocols.Entities.ProtocolStatusEntity;
import slcd.boost.boost.Protocols.RegularMeetings.Enums.EProtocolStatus;
import slcd.boost.boost.Protocols.RegularMeetings.Repos.ProtocolStatusRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProtocolStatusRepository protocolStatusRepository;

    @Override
    public void run(String... args) throws Exception {

        if(protocolStatusRepository.count() == 0){
            for (EProtocolStatus name : EProtocolStatus.values()){
                var statusEntity = new ProtocolStatusEntity();
                statusEntity.setName(name);
                protocolStatusRepository.save(statusEntity);
            }
        }
    }
}
