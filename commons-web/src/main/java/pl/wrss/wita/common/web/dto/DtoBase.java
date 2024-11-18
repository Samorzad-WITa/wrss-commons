package pl.wrss.wita.common.web.dto;

import lombok.Data;
import pl.wrss.wita.common.model.entity.scheme.IdSupport;

import java.util.UUID;

@Data
public abstract class DtoBase implements IdSupport {

    private UUID id;
}
