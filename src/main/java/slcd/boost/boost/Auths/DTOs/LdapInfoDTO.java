package slcd.boost.boost.Auths.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.nio.ByteBuffer;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LdapInfoDTO {
    private String uuid;
    private String username;
    private String email;

    public static LdapInfoDTO mapToLdapInfo(Attributes attrs) throws NamingException {
        String username = attrs.get("sAMAccountName").get().toString();
        String email = attrs.get("mail").get().toString();

        String objectGUIDString = String.valueOf(attrs.get("objectGUID"));
        String uuid = null;
        if(objectGUIDString != null) {
            byte[] objectGuidBytes = objectGUIDString.getBytes();
            uuid = UUID.nameUUIDFromBytes(
                    ByteBuffer
                            .wrap(objectGuidBytes)
                            .array()
            ).toString();
        }

        return new LdapInfoDTO(
                uuid,
                username,
                email
        );
    }
}
