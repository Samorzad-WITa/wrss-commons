package pl.wrss.wita.common.model.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.wrss.wita.common.model.entity.EntityBase;

import java.util.List;

public class TypedPageImpl<T extends EntityBase> extends PageImpl<T> implements TypedPage<T> {

    private final Class<T> type;

    public TypedPageImpl(Class<T> type, List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
        this.type = type;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    public static <T extends EntityBase> TypedPage<T> from(Page<T> page, Class<T> type) {
        return new TypedPageImpl<>(type, page.getContent(), page.getPageable(), page.getTotalElements());
    }
}
