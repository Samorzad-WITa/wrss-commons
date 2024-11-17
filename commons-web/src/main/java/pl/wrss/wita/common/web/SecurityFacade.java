package pl.wrss.wita.common.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pl.wrss.wita.common.model.entity.User;
import pl.wrss.wita.common.model.enums.Permission;
import pl.wrss.wita.common.security.AuthenticatedUser;
import pl.wrss.wita.common.security.permission.PermissionAuthority;
import pl.wrss.wita.common.service.PermissionService;
import pl.wrss.wita.common.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class SecurityFacade implements InitializingBean {

    private final PermissionService permissionService;
    private final UserService userService;

    private boolean unrestrictedAccess = true;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public void checkAccess(@NonNull Permission permission) {
        if(!unrestrictedAccess && !hasAccess(permission)) {
            throw new AccessDeniedException(String.format("Permission %s not granted", permission));
        }
    }

    public boolean hasAccess(Permission permission) {
        var authorities = getPermissionAuthorities();
        return permissionService.hasAccess(permission, authorities);
    }

    protected Collection<PermissionAuthority> getPermissionAuthorities() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .filter(PermissionAuthority.class::isInstance)
                .map(PermissionAuthority.class::cast)
                .collect(Collectors.toList());
    }

    public User getAuthenticatedUser() {
        return getAuthenticatedUser(false);
    }

    public User getAuthenticatedUserReference() {
        return getAuthenticatedUser(true);
    }

    private User getAuthenticatedUser(boolean reference) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            throw new IllegalStateException("No authenticated user available.");
        }
        var principal = authentication.getPrincipal();
        if(!(principal instanceof AuthenticatedUser)) {
            throw new IllegalStateException("Principal is not of type AuthenticatedUser.");
        }
        var userId = ((AuthenticatedUser) authentication.getPrincipal()).getUserId();
        return reference
                ? userService.getReference(userId)
                : userService.findById(userId);
    }
}
