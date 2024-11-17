package pl.wrss.wita.common.web.exception;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApplicationError implements ErrorDescriptor {
    ;

    private final HttpStatus defaultStatus;

    private final String code;

    private final String defaultMessage;

    ApplicationError(HttpStatus defaultStatus) {
        this(defaultStatus, null, null);
    }

    ApplicationError(HttpStatus defaultStatus, String defaultMessage) {
        this(defaultStatus, null, defaultMessage);
    }

    ApplicationError(HttpStatus defaultStatus, String code, String defaultMessage) {
        this.defaultStatus = defaultStatus;
        if(code == null) {
            code = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.name());
        }
        if(defaultMessage == null) {
            defaultMessage = code;
        }
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
