package pl.wrss.wita.common.model.repository;


import pl.wrss.wita.common.model.entity.User;
import pl.wrss.wita.common.model.filter.UserFilter;
import pl.wrss.wita.common.model.querydsl.FilterableRepository;

public interface UserRepository extends FilterableRepository<User, UserFilter> {
}
