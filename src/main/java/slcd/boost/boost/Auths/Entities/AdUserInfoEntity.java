package slcd.boost.boost.Auths.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "f_ad_user_infos")
public class AdUserInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 255, name = "uuid")
    private String uuid;

    @Column(length = 50, name = "username")
    private String username;
}
