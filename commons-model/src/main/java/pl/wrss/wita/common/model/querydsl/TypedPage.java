package pl.wrss.wita.common.model.querydsl;

import org.springframework.data.domain.Page;
import pl.wrss.wita.common.model.entity.EntityBase;

public interface TypedPage<T extends EntityBase> extends Page<T> {

    Class<T> getType();
}
