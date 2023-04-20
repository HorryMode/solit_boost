package slcd.boost.boost.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import slcd.boost.boost.Models.Enum.EProtocolStatus;
import slcd.boost.boost.Models.Enum.ERole;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "s_protocols_status")
public class ProtocolStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EProtocolStatus name;
}
