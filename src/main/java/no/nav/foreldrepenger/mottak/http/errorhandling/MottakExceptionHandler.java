package no.nav.foreldrepenger.mottak.http.errorhandling;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MottakExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MottakExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return logAndHandle(UNPROCESSABLE_ENTITY, e, request, validationErrors(e));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return logAndHandle(UNPROCESSABLE_ENTITY, e, request, getRootCauseMessage(e));
    }

    @ExceptionHandler(value = { RemoteUnavailableException.class })
    protected ResponseEntity<Object> handleRemoteUnavailable(RemoteUnavailableException e, WebRequest request) {
        return logAndHandle(INTERNAL_SERVER_ERROR, e, request, getRootCauseMessage(e));
    }

    @ExceptionHandler(value = { NotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(RemoteUnavailableException e, WebRequest request) {
        return logAndHandle(NOT_FOUND, e, request, getRootCauseMessage(e));
    }

    @ExceptionHandler(value = { ForbiddenException.class })
    protected ResponseEntity<Object> handleUnauthorized(ForbiddenException e, WebRequest request) {
        return logAndHandle(UNAUTHORIZED, e, request, getRootCauseMessage(e));
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Object> handleUncaught(Exception e, WebRequest request) {
        return logAndHandle(INTERNAL_SERVER_ERROR, e, request, getRootCauseMessage(e));
    }

    private ResponseEntity<Object> logAndHandle(HttpStatus status, Exception e, WebRequest req, String... messages) {
        return logAndHandle(status, e, req, asList(messages));
    }

    private ResponseEntity<Object> logAndHandle(HttpStatus status, Exception e, WebRequest req, List<String> messages) {
        LOG.warn("{}", messages, e);
        return handleExceptionInternal(e, new ApiError(status, e, messages), new HttpHeaders(), status, req);
    }

    private static List<String> validationErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors()
                .stream()
                .map(MottakExceptionHandler::errorMessage)
                .collect(toList());
    }

    private static String errorMessage(FieldError error) {
        return error.getField() + " " + error.getDefaultMessage();
    }
}
