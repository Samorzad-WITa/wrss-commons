package pl.wrss.wita.common.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

@Getter
public class JwtAuthenticatedUserToken extends AbstractAuthenticationToken {

    private final Jwt jwt;

    private final AuthenticatedUser authenticatedUser;

    public JwtAuthenticatedUserToken(AuthenticatedUser authenticatedUser, Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwt = jwt;
        this.authenticatedUser = authenticatedUser;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return getAuthenticatedUser();
    }
}
