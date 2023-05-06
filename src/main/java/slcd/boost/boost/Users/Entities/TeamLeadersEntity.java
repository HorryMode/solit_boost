package slcd.boost.boost.Users.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "f_team_leaders")
public class TeamLeadersEntity {

    @EmbeddedId
    private TeamLeaderId id;

    private boolean isMainLeader;

    private boolean archived;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable=false, updatable=false)
    private UserEntity user;
}
