package pl.wrss.wita.common.web.exception;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ControllerAdvice
public class ExceptionMapper extends ResponseEntityExceptionHandler implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    protected ErrorDescriptor findErrorDescriptor(Exception exception) {
        if(exception instanceof ApplicationException applicationException) {
            return applicationException.getDescriptor();
        }
        return null;
    }

    protected ResolvedError resolveError(Exception exception) {
        ErrorDescriptor errorDescriptor = findErrorDescriptor(exception);
        String message = exception.getMessage();
        String code = null;
        HttpStatus status = null;
        Map<String, Object> additionalInfo = null;
        if (exception instanceof ApplicationException) {
            additionalInfo = ((ApplicationException)exception).getAdditionalInfo();
            code = ((ApplicationException)exception).getCode();
            status = ((ApplicationException)exception).getStatus();
        }
        if (message == null && errorDescriptor != null) {
            message = errorDescriptor.getDefaultMessage();
        }
        if (code == null && errorDescriptor != null) {
            code = errorDescriptor.getCode();
        }
        if (status == null && errorDescriptor != null) {
            status = errorDescriptor.getDefaultStatus();
        }
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String requestId = null;
        ErrorDto errorBody = new ErrorDto();
        errorBody.setAdditionalInfo(additionalInfo);
        errorBody.setRequestId(requestId);
        errorBody.setCode(code);
        errorBody.setMessage(message);

        attachBindingResult(errorBody, exception);

        ResolvedError resolvedError = new ResolvedError();
        resolvedError.setError(errorBody);
        resolvedError.setStatus(status);

        return resolvedError;
    }

    protected void attachBindingResult(ErrorDto errorDto, Exception exception) {
        BindingResult bindingResult = getBindingResult(exception);
        if (bindingResult == null) {
            return;
        }
        List<FieldErrorDto> fieldErrorDtos = new ArrayList<>(bindingResult.getFieldErrorCount());
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            FieldErrorDto fieldErrorDto = new FieldErrorDto();
            fieldErrorDto.setCode(fieldError.getCode());
            fieldErrorDto.setField(fieldError.getField());
            fieldErrorDto.setMessage(fieldError.getDefaultMessage());
            fieldErrorDto.setObjectName(fieldError.getObjectName());
            fieldErrorDto.setRejectedValue(fieldError.getRejectedValue());
            fieldErrorDtos.add(fieldErrorDto);
        }
        errorDto.setFieldErrors(fieldErrorDtos.stream().toArray(FieldErrorDto[]::new));
    }

    protected BindingResult getBindingResult(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException)exception).getBindingResult();
        }
        if (exception instanceof BindException) {
            return ((BindException)exception).getBindingResult();
        }
        if (exception instanceof ApplicationException) {
            return ((ApplicationException)exception).getBindingResult();
        }
        return null;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAnyException(Exception exception, WebRequest request) {
        return handleAnyException(exception, request, null);
    }

    public ResponseEntity<Object> handleAnyException(Exception exception, WebRequest request, Function<ErrorDto, Object> errorWrapper) {
        ResolvedError resolvedError = resolveError(exception);
        ErrorDto errorDto = resolvedError.getError();
        HttpStatus status = resolvedError.getStatus();
        Object body = errorWrapper != null ? errorWrapper.apply(errorDto) : errorDto;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Entity", "Error");
        return handleExceptionInternal(exception, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleAnyException(exception, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleAnyException(exception, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (statusCode.is5xxServerError()) {
            logger.error("Unhandled server error.", ex);
        } else if (statusCode.is4xxClientError()) {
            logger.info("Client error.", ex);
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
}
