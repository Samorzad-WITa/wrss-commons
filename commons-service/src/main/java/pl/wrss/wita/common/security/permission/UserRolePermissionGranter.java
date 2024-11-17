package pl.wrss.wita.common.security.permission;

import org.springframework.security.core.GrantedAuthority;
import pl.wrss.wita.common.model.entity.UserRole;

import java.util.Set;

public interface UserRolePermissionGranter extends PermissionGranter {

    void handle(UserRole userRole, Set<GrantedAuthority> grantedAuthorities);
}
