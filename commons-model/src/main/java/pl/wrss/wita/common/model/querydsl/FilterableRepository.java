package pl.wrss.wita.common.model.querydsl;

import org.springframework.data.repository.NoRepositoryBean;
import pl.wrss.wita.common.model.entity.EntityBase;
import pl.wrss.wita.common.model.filter.Filter;

import java.util.List;

@NoRepositoryBean
public interface FilterableRepository<T extends EntityBase, F extends Filter> extends EntityRepository<T>, FilterExecutor<T, F> {

    List<T> findAll(F filter);
}
