package pl.wrss.wita.common.web.exception;

import org.springframework.http.HttpStatus;

public interface ErrorDescriptor {

    HttpStatus getDefaultStatus();
    String getCode();
    String getDefaultMessage();
}
