package pl.wrss.wita.common.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.wrss.wita.common.model.enums.UserType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "`user`")
@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class User extends EntityBase {

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserType type;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<UserRole> roles = new HashSet<>();
}
