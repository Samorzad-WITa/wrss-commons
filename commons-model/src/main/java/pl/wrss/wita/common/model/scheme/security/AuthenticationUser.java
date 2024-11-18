package pl.wrss.wita.common.model.scheme.security;

import pl.wrss.wita.common.model.scheme.IdSupport;
import pl.wrss.wita.common.model.scheme.UserRole;

import java.util.Set;

public interface AuthenticationUser extends IdSupport {
    String getEmail();
    void setEmail(String email);

    <E extends UserRole> Set<E> getRoles();
    <E extends UserRole> void setRoles(Set<E> roles);
}
