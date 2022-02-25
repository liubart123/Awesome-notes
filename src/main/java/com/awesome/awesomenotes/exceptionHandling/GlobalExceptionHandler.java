package com.awesome.awesomenotes.exceptionHandling;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

import com.awesome.awesomenotes.authentication.AuthException;
import com.awesome.awesomenotes.exception.ElementCreationException;
import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String TRACE = "trace";

    @Value("${allow-error-trace-in-responses:false}")
    private boolean printStackTrace;

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Validation error. Check 'errors' field for details.");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        // return buildErrorResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ElementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoSuchElementFoundException(ElementNotFoundException e,
            WebRequest request) {
        log.error("Failed to find the requested element", e.getMessage());
        return buildErrorResponse(e, e.getStatus(), request);
    }

    @ExceptionHandler(ElementCreationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoSuchElementFoundException(ElementCreationException e,
            WebRequest request) {
        log.error("Failed to create the requested element", e.getMessage());
        return buildErrorResponse(e, e.getStatus(), request);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> handleNoSuchElementFoundException(AuthException e,
            WebRequest request) {
        log.error("Auth exception: ", e.getMessage());
        return buildErrorResponse(e, e.getStatus(), request);
    }

    @ExceptionHandler(LackOfPermissionsException.class)
    public ResponseEntity<Object> handleLackOfPermissionsException(LackOfPermissionsException e,
            WebRequest request) {
        log.error("Lack of permissions exception: ", e.getMessage());
        return buildErrorResponse(e, e.getStatus(), request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        log.error("Unknown error occurred", exception);
        return buildErrorResponse(exception, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception,
            HttpStatus httpStatus,
            WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (printStackTrace && isTraceOn(request)) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private boolean isTraceOn(WebRequest request) {
        String[] value = request.getParameterValues(TRACE);
        return Objects.nonNull(value)
                && value.length > 0
                && value[0].contentEquals("true");
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        return buildErrorResponse(ex, status, request);
    }
}