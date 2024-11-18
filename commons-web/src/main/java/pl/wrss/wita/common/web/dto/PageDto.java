package pl.wrss.wita.common.web.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import pl.wrss.wita.common.mapping.Mapper;
import pl.wrss.wita.common.mapping.MappingProperties;

import java.util.Collection;

@Data
public class PageDto<T> {

    @Data
    @Accessors(chain = true)
    public static abstract class Properties implements MappingProperties {
        private Mapper contentMapper;
    }

    private Collection<T> content;

    private Long totalElements;

    private int pageNumber;

    private Integer totalPages;

    private int numberOfElements;
}
