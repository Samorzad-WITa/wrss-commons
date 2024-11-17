package pl.wrss.wita.common.web.exception;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FieldErrorDto {

    private String field;

    private String message;

    private String code;

    private String objectName;

    private Object rejectedValue;
}
