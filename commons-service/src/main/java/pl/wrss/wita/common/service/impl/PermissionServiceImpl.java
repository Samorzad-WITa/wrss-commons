package pl.wrss.wita.common.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import pl.wrss.wita.common.model.enums.Permission;
import pl.wrss.wita.common.security.permission.PermissionAuthority;
import pl.wrss.wita.common.service.PermissionService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public boolean hasAccess(Permission permission, Collection<PermissionAuthority> authorities) {
        for(var authority : authorities) {
            if(authority.getAuthority().equals(permission.name())) {
                return true;
            }
        }
        return false;
    }
}
