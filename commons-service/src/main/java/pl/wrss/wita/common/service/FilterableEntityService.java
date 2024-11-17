package pl.wrss.wita.common.service;


import pl.wrss.wita.common.model.entity.EntityBase;
import pl.wrss.wita.common.model.filter.Filter;
import pl.wrss.wita.common.model.querydsl.TypedPage;

import java.util.List;

public interface FilterableEntityService<E extends EntityBase, F extends Filter> extends EntityService<E> {

    List<E> getList(F filter);

    TypedPage<E> getPage(F filter);
}
