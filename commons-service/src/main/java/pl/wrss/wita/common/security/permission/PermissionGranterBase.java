package pl.wrss.wita.common.security.permission;

import org.springframework.security.core.GrantedAuthority;
import pl.wrss.wita.common.model.enums.Permission;

import java.util.Set;

public abstract class PermissionGranterBase {

    protected void grantSystemAccess(Set<GrantedAuthority> authorities) {
        for(Permission permission : Permission.values()) {
            authorities.add(new PermissionAuthority(permission));
        }
    }

    protected void grant(Set<GrantedAuthority> authorities, PermissionAuthority permissionAuthority) {
        authorities.add(permissionAuthority);
    }
}
