package pl.wrss.wita.common.model.querydsl;

import jakarta.persistence.EntityManager;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import pl.wrss.wita.common.model.entity.EntityBase;
import pl.wrss.wita.common.model.filter.Filter;

import java.util.List;

@NoRepositoryBean
public interface FilterExecutor<T extends EntityBase, F extends Filter> extends QuerydslPredicateExecutor<T> {

    EntityManager getEntityManager();

    List<T> findAll(F filter);
    TypedPage<T> findPage(F filter);
}
