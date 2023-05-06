package slcd.boost.boost.Auths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Auths.DTOs.LdapInfoDTO;
import slcd.boost.boost.Auths.DTOs.LdapInfoMapper;
import slcd.boost.boost.Auths.Entities.AdUserInfoEntity;
import slcd.boost.boost.Auths.Repos.AdUserInfoEntityRepository;

import javax.naming.directory.Attributes;
import java.util.List;
import java.util.Objects;

@Service
public class LdapService {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private LdapConfig ldapConfig;

    @Autowired
    private AdUserInfoEntityRepository adUserInfoEntityRepository;

    public boolean authenticate(String username, String password){
        String userDn = ldapConfig.filterUserDn(username);
        return ldapTemplate.authenticate("",userDn, password);
    }

    public LdapInfoDTO findByUsername(String username){
        String userDn = ldapConfig.filterUserDn(username);
        List<LdapInfoDTO> ldapInfos = ldapTemplate.search("", userDn, LdapInfoMapper::mapToLdapInfo);

        return ldapInfos.get(0);
    }

    public void synchronizeAdUserInfo(LdapInfoDTO ldapInfoDTO){
        String uuid = ldapInfoDTO.getUuid();
        String username = ldapInfoDTO.getUsername();

        AdUserInfoEntity adUserInfoEntity = adUserInfoEntityRepository.findByUuid(uuid);
        if(Objects.isNull(adUserInfoEntity)) {
            var newAdUserInfoEntity = new AdUserInfoEntity();
            newAdUserInfoEntity.setUuid(uuid);
            newAdUserInfoEntity.setUsername(username);
            adUserInfoEntityRepository.save(newAdUserInfoEntity);
        }
    }
}
