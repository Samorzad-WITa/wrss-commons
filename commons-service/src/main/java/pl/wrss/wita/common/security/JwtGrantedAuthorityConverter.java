package pl.wrss.wita.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.wrss.wita.common.model.enums.Permission;
import pl.wrss.wita.common.security.permission.PermissionAuthority;

import java.util.regex.Pattern;

@Component
public class JwtGrantedAuthorityConverter {

    public static final String CLAIM_PREFIX = "pa-";
    private final Pattern permissionAuthorityPattern = Pattern.compile("^" + CLAIM_PREFIX + "([A-Za-z\\d]+)");

    public GrantedAuthority toAuthority(String claim) {
        var permissionAuthorityMatcher = permissionAuthorityPattern.matcher(claim);
        if(permissionAuthorityMatcher.matches()) {
            var permissionRaw = permissionAuthorityMatcher.group(1);
            var permission = Enum.valueOf(Permission.class, permissionRaw);
            return new PermissionAuthority(permission);
        }
        return new SimpleGrantedAuthority(claim);
    }

    public String toClaim(GrantedAuthority authority) {
        if(authority instanceof PermissionAuthority permissionAuthority) {
            return new StringBuilder()
                    .append(CLAIM_PREFIX)
                    .append(permissionAuthority.getAuthority())
                    .toString();
        }
        return authority.getAuthority();
    }
}
