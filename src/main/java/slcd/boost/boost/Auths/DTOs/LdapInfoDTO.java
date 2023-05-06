package slcd.boost.boost.Auths.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class LdapInfoDTO {
    private String uuid;
    private String username;
}
