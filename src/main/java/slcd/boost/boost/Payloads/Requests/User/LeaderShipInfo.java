package slcd.boost.boost.Payloads.Requests.User;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderShipInfo {
    @NotNull
    private Long userId;
    @NotNull
    private boolean isMainLeader;
}
