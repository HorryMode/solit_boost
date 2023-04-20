package slcd.boost.boost.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "f_team_leaders")
public class TeamLeadersEntity {

    @EmbeddedId
    private TeamLeaderId id;

    private boolean isMainLeader;

    private boolean archived;
}
