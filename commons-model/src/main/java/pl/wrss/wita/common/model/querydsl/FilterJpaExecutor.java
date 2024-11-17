package pl.wrss.wita.common.model.querydsl;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import pl.wrss.wita.common.model.entity.EntityBase;
import pl.wrss.wita.common.model.filter.Filter;
import pl.wrss.wita.common.model.filter.binder.FilterBinder;

import java.util.List;

public class FilterJpaExecutor<T extends EntityBase, F extends Filter> extends QuerydslJpaPredicateExecutor<T> implements FilterExecutor<T, F>, PathProvider<T> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityPath<T> path;
    private final Querydsl querydsl;
    private final EntityManager entityManager;
    private final FilterBinder<T, F, EntityPath<T>> filterBinder;

    public FilterJpaExecutor(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager, EntityPathResolver resolver, @Nullable CrudMethodMetadata metadata, FilterBinder<T, F, EntityPath<T>> filterBinder) {
        super(entityInformation, entityManager, resolver, metadata);
        this.entityInformation = entityInformation;
        this.path = resolver.createPath(entityInformation.getJavaType());
        this.querydsl = new Querydsl(entityManager, new PathBuilder<T>(path.getType(), path.getMetadata()));
        this.entityManager = entityManager;
        this.filterBinder = filterBinder;
    }

    protected Predicate createPredicate(F filter) {
        return filterBinder.bindFilter(filter, path);
    }

    protected Pageable createPageable(F filter) {
        var pageOffset = filter.getPageOffset();
        var pageLimit = filter.getPageLimit();
        var sort = filterBinder.bindSort(filter, path);
        return new OffsetPageable(Math.max(pageOffset, 0), pageLimit > 0 ? pageLimit : Integer.MAX_VALUE, sort);
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<T> findAll(F filter) {
        return createQuery(createPredicate(filter)).select(path).fetch();
    }

    @Override
    public TypedPage<T> findPage(F filter) {
        final var predicate = filterBinder.bindFilter(filter, path);
        final var countQuery = createCountQuery(predicate);
        final var pageable = createPageable(filter);
        final var query = querydsl.applyPagination(pageable, createQuery(predicate).select(path));

        return TypedPageImpl.from(PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchCount), entityInformation.getJavaType());
    }

    @Override
    public EntityPath<T> getPath() {
        return path;
    }
}
