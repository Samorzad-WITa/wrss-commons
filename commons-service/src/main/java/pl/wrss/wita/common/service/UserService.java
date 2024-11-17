package pl.wrss.wita.common.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.wrss.wita.common.model.entity.User;
import pl.wrss.wita.common.model.filter.UserFilter;

public interface UserService extends UserDetailsService, FilterableEntityService<User, UserFilter> {

}
