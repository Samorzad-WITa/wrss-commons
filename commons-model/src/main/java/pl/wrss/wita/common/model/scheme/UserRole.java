package pl.wrss.wita.common.model.scheme;

import pl.wrss.wita.common.model.scheme.security.AuthenticationRole;
import pl.wrss.wita.common.model.scheme.security.AuthenticationUser;

public interface UserRole {

    <E extends AuthenticationUser> E getUser();
    <E extends AuthenticationUser> void setUser(E user);

    <E extends AuthenticationRole> E getRole();
    <E extends AuthenticationRole> void setRole(E role);
}
