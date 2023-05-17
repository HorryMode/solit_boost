package slcd.boost.boost.Auths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Auths.DTOs.LdapInfoDTO;
import slcd.boost.boost.Auths.Entities.AdUserInfoEntity;
import slcd.boost.boost.Auths.Repos.AdUserInfoEntityRepository;
import slcd.boost.boost.Users.Entities.UserEntity;

import java.util.List;
import java.util.UUID;

@Service
public class LdapService {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private LdapConfig ldapConfig;

    @Autowired
    private AdUserInfoEntityRepository adUserInfoEntityRepository;

    public boolean authenticate(String username, String password){
        String userDn = ldapConfig.filterUserDnByUsername(username);
        return ldapTemplate.authenticate("",userDn, password);
    }

    public LdapInfoDTO findByUsername(String username){
        String userDn = ldapConfig.filterUserDnByUsername(username);
        List<LdapInfoDTO> ldapInfos = ldapTemplate.search("", userDn, LdapInfoDTO::mapToLdapInfo);

        return ldapInfos.get(0);
    }

    public LdapInfoDTO findByEmail(String email){
        String userDn = ldapConfig.filterUserDnByEmail(email);
        List<LdapInfoDTO> ldapInfos = ldapTemplate.search("", userDn, LdapInfoDTO::mapToLdapInfo);

        return ldapInfos.get(0);
    }

    public void saveAdUserInfo(LdapInfoDTO ldapInfoDTO, UserEntity user){
            var newAdUserInfoEntity = new AdUserInfoEntity();
            newAdUserInfoEntity.setUuid(
                    UUID.fromString(ldapInfoDTO.getUuid())
            );
            newAdUserInfoEntity.setUsername(
                    ldapInfoDTO.getUsername()
            );
            newAdUserInfoEntity.setUser(user);
            adUserInfoEntityRepository.save(newAdUserInfoEntity);
    }
}
