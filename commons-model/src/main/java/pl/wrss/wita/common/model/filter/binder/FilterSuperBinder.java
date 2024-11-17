package pl.wrss.wita.common.model.filter.binder;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.RequiredArgsConstructor;
import pl.wrss.wita.common.model.filter.Filter;
import pl.wrss.wita.common.model.querydsl.QuerydslHelper;

@RequiredArgsConstructor
public abstract class FilterSuperBinder<T, F extends Filter, Q extends EntityPathBase<?>, S extends FilterBinderBase> extends FilterBinderBase<T, F, Q> {

    private final S superBinder;
    private final QuerydslHelper querydslHelper;

    @Override
    protected Predicate getBaseDatePredicate(F filter, Q path) {
        EntityPathBase<?> superPath = querydslHelper.getSuperPath(path);
        return superBinder.getDataPredicate(filter, superPath);
    }

    @Override
    protected SortDetails bindSort(Q path, String sortSchema) {
        EntityPathBase<?> superPath = querydslHelper.getSuperPath(path);
        return superBinder.bindSort(superPath, sortSchema);
    }

    @Override
    protected SortDetails bindSort(Q path, String sortSchema, F filter) {
        EntityPathBase<?> superPath = querydslHelper.getSuperPath(path);
        return superBinder.bindSort(superPath, sortSchema, filter);
    }
}
