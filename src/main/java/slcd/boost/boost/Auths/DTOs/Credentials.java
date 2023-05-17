package slcd.boost.boost.Auths.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Credentials {
    @NotNull
    private String credentials;
}
