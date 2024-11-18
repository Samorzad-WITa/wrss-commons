package pl.wrss.wita.common.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.wrss.wita.common.model.querydsl.TypedPage;
import pl.wrss.wita.common.mapping.MapperBase;
import pl.wrss.wita.common.web.dto.PageDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class TypedPageMapper extends MapperBase<TypedPage, PageDto, PageDto.Properties> {

    @Override
    public void transform(TypedPage source, PageDto destination, PageDto.Properties properties) {
        super.transform(source, destination, properties);

        destination.setContent(properties.getContentMapper().map(source.getContent(), properties));
        destination.setPageNumber(source.getNumber());
        destination.setTotalPages(source.getTotalPages());
        destination.setNumberOfElements(source.getNumberOfElements());
        destination.setTotalElements(source.getTotalElements());
    }
}
