package slcd.boost.boost.Payloads.Responses.Auth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;

    public JwtResponse(String accessToken) {
        this.token = accessToken;
    }
}
