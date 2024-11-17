package pl.wrss.wita.common.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import pl.wrss.wita.common.model.enums.UserType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClaimsAuthenticatedUserTokenConverter implements Converter<Claims, UsernamePasswordAuthenticationToken> {

    private final JwtGrantedAuthorityConverter authorityConverter;

    @Override
    public UsernamePasswordAuthenticationToken convert(Claims claims) {
        var email = (String) claims.get("email");
        var userId = UUID.fromString((String) claims.get("uid"));
        var userType = Enum.valueOf(UserType.class, (String) claims.get("userType"));
        var authorities = extractAuthorities(claims);
        var authenticatedUser = AuthenticatedUser.builder()
                .userId(userId)
                .email(email)
                .userType(userType)
                .build();
        return new UsernamePasswordAuthenticationToken(authenticatedUser, authorities);
    }

    protected Collection<GrantedAuthority> extractAuthorities(Claims claims) {
        var authorities = new HashSet<GrantedAuthority>();
        var authorityClaims = claims.get("authorities");
        if(authorityClaims == null) {
            return Collections.emptySet();
        }
        for(var authorityClaim : (Collection<String>) authorityClaims) {
            authorities.add(authorityConverter.toAuthority(authorityClaim));
        }
        return authorities;
    }
}
