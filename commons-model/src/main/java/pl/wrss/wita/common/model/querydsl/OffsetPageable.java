package pl.wrss.wita.common.model.querydsl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OffsetPageable extends PageRequest {

    private final int offset;

    public OffsetPageable(int offset, int limit) {
        this(offset, limit, null);
    }

    public OffsetPageable(int offset, int limit, Sort sort) {
        super(offset, limit, sort);
        this.offset = offset;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    public int getPageNumber() {
        return this.offset / this.getPageSize();
    }
}
