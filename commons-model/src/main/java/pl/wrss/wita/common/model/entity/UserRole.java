package pl.wrss.wita.common.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class UserRole extends EntityBase {

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(insertable = false, updatable = false, name = "user_id")
    private UUID userId;

    @ManyToOne
    @JoinColumn(nullable = false, name = "role_id")
    private Role role;

    @Column(insertable = false, updatable = false, name = "role_id")
    private UUID roleId;

    public void setUser(User user) {
        this.user = user;
        this.userId = user == null ? null : user.getId();
    }

    public void setRole(Role role) {
        this.role = role;
        this.roleId = role == null ? null : role.getId();
    }
}
