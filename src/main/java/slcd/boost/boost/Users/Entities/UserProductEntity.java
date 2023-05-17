package slcd.boost.boost.Users.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "f_users_products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProductEntity {

    @EmbeddedId
    private UserProductId id;

    @Column(name = "role")
    private String role;

    @Column(name = "busy", nullable = false)
    private Long busy;;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable=false, updatable=false)
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable=false, updatable=false)
    private UserEntity user;
}
