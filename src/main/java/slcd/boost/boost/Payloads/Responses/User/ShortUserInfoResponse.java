package slcd.boost.boost.Payloads.Responses.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ShortUserInfoResponse {

    private String name;
    private String attestationDate;
}
