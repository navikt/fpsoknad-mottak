package no.nav.foreldrepenger.errorhandling;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.security.oidc.exceptions.OIDCTokenValidatorException;
import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;

@ControllerAdvice
public class OppslagExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OppslagExceptionHandler.class);

    private static final Counter invalidRequestsCounter = Metrics.counter("errors.request.invalid");
    private static final Counter unauthorizedCounter = Metrics.counter("errors.lookup.unauthorized");
    private static final Counter unauthenticatedCounter = Metrics.counter("errors.lookup.unauthenticated");

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return logAndRespond(UNPROCESSABLE_ENTITY, e, request, validationErrors(e));
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleValidationException(ConstraintViolationException e, WebRequest req) {
        invalidRequestsCounter.increment();
        return logAndRespond(UNPROCESSABLE_ENTITY, e, req, getRootCauseMessage(e));
    }

    @ExceptionHandler({ IncompleteRequestException.class })
    public ResponseEntity<Object> handleIncompleteRequestException(IncompleteRequestException e, WebRequest req) {
        invalidRequestsCounter.increment();
        return logAndRespond(BAD_REQUEST, e, req, getRootCauseMessage(e));
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e, WebRequest req) {
        return logAndRespond(NOT_FOUND, e, req, getRootCauseMessage(e));
    }

    @ExceptionHandler({ OIDCUnauthorizedException.class })
    public ResponseEntity<Object> handleOIDCUnauthorizedException(OIDCUnauthorizedException e, WebRequest req) {
        unauthorizedCounter.increment();
        return logAndRespond(UNAUTHORIZED, e, req, getRootCauseMessage(e));
    }

    @ExceptionHandler({ OIDCTokenValidatorException.class })
    public ResponseEntity<Object> handleUnauthenticatedOIDCException(OIDCTokenValidatorException e, WebRequest req) {
        unauthenticatedCounter.increment();
        return logAndRespond(FORBIDDEN, e, req, getRootCauseMessage(e),
                e.getExpiryDate() != null ? e.getExpiryDate().toString() : null);
    }

    @ExceptionHandler({ UnauthorizedException.class })
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e, WebRequest req) {
        unauthorizedCounter.increment();
        return logAndRespond(UNAUTHORIZED, e, req, getRootCauseMessage(e));
    }

    @ExceptionHandler({ UnauthenticatedException.class })
    public ResponseEntity<Object> handleUnauthenticatedException(UnauthenticatedException e, WebRequest req) {
        unauthenticatedCounter.increment();
        return logAndRespond(FORBIDDEN, e, req, getRootCauseMessage(e));
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> catchAll(Exception e, WebRequest req) {
        return logAndRespond(INTERNAL_SERVER_ERROR, e, req, getRootCauseMessage(e));
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, Exception e, WebRequest req, String... messages) {
        return logAndRespond(status, e, req, asList(messages));
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, Exception e, WebRequest req,
            List<String> messages) {
        LOG.warn("{}", messages, e);
        return handleExceptionInternal(e,
                new ApiError(status, e, messages.stream().filter(s -> s != null).collect(toList())),
                new HttpHeaders(), status, req);
    }

    private static String getRootCauseMessage(Exception e) {
        return getMostSpecificCause(e).getMessage();
    }

    private static List<String> validationErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors()
                .stream()
                .map(OppslagExceptionHandler::errorMessage)
                .collect(toList());
    }

    private static String errorMessage(FieldError error) {
        return error.getField() + " " + error.getDefaultMessage();
    }

}
