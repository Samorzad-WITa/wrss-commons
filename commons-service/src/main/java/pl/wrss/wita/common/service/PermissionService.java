package pl.wrss.wita.common.service;

import pl.wrss.wita.common.model.enums.Permission;
import pl.wrss.wita.common.security.permission.PermissionAuthority;

import java.util.Collection;

public interface PermissionService {

    boolean hasAccess(Permission permission, Collection<PermissionAuthority> authorities);
}
