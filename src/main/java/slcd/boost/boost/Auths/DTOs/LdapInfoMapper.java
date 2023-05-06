package slcd.boost.boost.Auths.DTOs;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.nio.ByteBuffer;
import java.util.UUID;

public record LdapInfoMapper() {
    public static LdapInfoDTO mapToLdapInfo(Attributes attrs) throws NamingException {
        String username = attrs.get("sAMAccountName").get().toString();

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
                username,
                uuid
        );
    }

}
