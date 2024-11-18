package pl.wrss.wita.common.service.impl;

import com.google.common.base.CaseFormat;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.wrss.wita.common.model.entity.QUser;
import pl.wrss.wita.common.model.entity.User;
import pl.wrss.wita.common.model.entity.UserRole;
import pl.wrss.wita.common.model.filter.UserFilter;
import pl.wrss.wita.common.model.repository.UserRepository;
import pl.wrss.wita.common.security.AuthenticatedUser;
import pl.wrss.wita.common.security.permission.UserRolePermissionGranter;
import pl.wrss.wita.common.service.UserService;

import java.util.Set;

@Service
public class UserServiceImpl extends FilterableEntityServiceBase<User, UserFilter> implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ApplicationContext applicationContext;

    public UserServiceImpl(UserRepository repository, ApplicationContext applicationContext) {
        super(repository);
        this.applicationContext = applicationContext;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var path = QUser.user;
        var tuple = new JPAQuery<>(entityManager).select(path.id, path.email, path.type).from(path).where(path.email.equalsIgnoreCase(username)).fetchOne();
        if(tuple == null) {
            throw new UsernameNotFoundException(String.format("User with email '%s' not found", username));
        }
        var userId = tuple.get(path.id);
        var type = tuple.get(path.type);
        return AuthenticatedUser.builder().userId(userId).email(username).userType(type).build();
    }

    protected void grant(Set<GrantedAuthority> authorities, User user) {
        for(var userRole : user.getRoles()) {
            var role = userRole.getRole();
            var granterNames = role.getPermissionGranters();
            grant(authorities, userRole, granterNames);
        }
    }

    protected void grant(Set<GrantedAuthority> authorities, UserRole userRole, String granterBeanName) {
        var granter = applicationContext.getBean(granterBeanName, UserRolePermissionGranter.class);
        granter.handle(userRole, authorities);
    }

    protected void grant(Set<GrantedAuthority> authorities, UserRole userRole, String... granterNames) {
        for (var granterName : granterNames) {
            var granterBeanName = getGranterBeanName(granterName);
            grant(authorities, userRole, granterBeanName);
        }
    }

    protected String getGranterBeanName(String granterName) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, granterName) + "PermissionGranter";
    }
}