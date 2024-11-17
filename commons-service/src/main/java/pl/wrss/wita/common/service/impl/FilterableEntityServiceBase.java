package pl.wrss.wita.common.service.impl;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import pl.wrss.wita.common.model.entity.EntityBase;
import pl.wrss.wita.common.model.filter.Filter;
import pl.wrss.wita.common.model.querydsl.FilterableRepository;
import pl.wrss.wita.common.model.querydsl.TypedPage;
import pl.wrss.wita.common.service.FilterableEntityService;

import java.util.List;
import java.util.UUID;

public abstract class FilterableEntityServiceBase<E extends EntityBase, F extends Filter> extends EntityServiceBase<E> implements FilterableEntityService<E, F> {

    public FilterableEntityServiceBase(FilterableRepository<E, F> repository) {
        super(repository);
    }

    public FilterableRepository<E, F> getRepository() {
        return (FilterableRepository<E, F>) super.getRepository();
    }

    @Override
    public List<E> getList(F filter) {
        return getRepository().findAll(filter);
    }

    @Override
    public TypedPage<E> getPage(F filter) {
        return getRepository().findPage(filter);
    }

    @Override
    public JPAQuery<?> createQuery() {
        return new JPAQuery<>(getEntityManager());
    }

    @Override
    public EntityManager getEntityManager() {
        return getRepository().getEntityManager();
    }

    @Override
    public void delete(E entity) {
        getRepository().delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        getRepository().deleteById(id);
    }
}
