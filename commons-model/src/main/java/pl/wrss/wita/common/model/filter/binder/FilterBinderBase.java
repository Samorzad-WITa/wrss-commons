package pl.wrss.wita.common.model.filter.binder;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import pl.wrss.wita.common.model.filter.Filter;
import pl.wrss.wita.common.utils.GenericHelper;

public abstract class FilterBinderBase<T, F extends Filter, Q extends EntityPathBase<?>> implements FilterBinder<T, F, Q> {

    @Data
    @SuperBuilder
    protected static class SortDetails {
        private final ComparableExpressionBase<?> path;
        private final OrderSpecifier.NullHandling nullHandling;
    }

    protected final Class<T> entityType;

    protected FilterBinderBase() {
        entityType = GenericHelper.getArgumentType(getClass(), "T");
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    @Override
    public Predicate bindFilter(F filter, Q path) {
        return getDataPredicate(filter, path);
    }

    public Predicate getDataPredicate(F filter, Q path) {
        return bindDataFilter(new PredicateBuilder<>(path, PredicateBuilder.Grouping.And, getBaseDatePredicate(filter, path)), filter);
    }

    protected Predicate getBaseDatePredicate(F filter, Q path) {
        return null;
    }

    protected Predicate bindDataFilter(PredicateBuilder<Q> builder, F filter) {
        return builder.build();
    }

    protected Sort getDefaultSort() {
        return Sort.unsorted();
    }

    @Override
    public Sort bindSort(F filter, Q path) {
        var sortSchema = filter.getSortSchema();
        if (sortSchema == null) {
            return getDefaultSort();
        }
        var sortDirection = filter.getSortDirection();
        if (sortDirection == null) {
            sortDirection = "asc";
        }
        var sortDetails =  bindSort(path, sortSchema, filter);
        var orderSpecifier = switch (sortDirection.toLowerCase()) {
            case "asc" -> sortDetails.path.asc();
            case "desc" -> sortDetails.path.desc();
            default -> throw new UnsupportedOperationException(String.format("Unsupported sort direction '%s'.", sortDirection));
        };
        if (OrderSpecifier.NullHandling.NullsFirst.equals(sortDetails.nullHandling)) {
            orderSpecifier = orderSpecifier.isAscending() ? orderSpecifier.nullsFirst() : orderSpecifier.nullsLast();
        } else if (OrderSpecifier.NullHandling.NullsLast.equals(sortDetails.nullHandling)) {
            orderSpecifier = orderSpecifier.isAscending() ? orderSpecifier.nullsLast() : orderSpecifier.nullsFirst();
        }
        return new QSort(orderSpecifier);
    }

    protected SortDetails bindSort(Q path, String sortSchema) {
        return throwNotSupportedSortSchemaException(sortSchema);
    }

    protected SortDetails bindSort(Q path, String sortSchema, F filter) {
        return bindSort(path, sortSchema);
    }

    protected SortDetails throwNotSupportedSortSchemaException(String sortSchema) {
        throw new UnsupportedOperationException(String.format("Sort schema '%s' not supported.", sortSchema));
    }
}
