package pl.wrss.wita.common.web.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResolvedError {

    private ErrorDto error;
    private HttpStatus status;
}
