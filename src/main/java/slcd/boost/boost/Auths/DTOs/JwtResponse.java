package slcd.boost.boost.Auths.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Data
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
}
