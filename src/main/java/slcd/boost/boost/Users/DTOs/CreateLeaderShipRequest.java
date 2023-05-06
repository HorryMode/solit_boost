package slcd.boost.boost.Users.DTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateLeaderShipRequest {
    @NotNull
    private Long teamLeaderId;

    @NotNull
    @NotEmpty
    private ArrayList<LeaderShipInfo> leaderShipInfos;
}
