package no.nav.foreldrepenger.errorhandling;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;

@ControllerAdvice
public class CommonControllerErrorHandlers {

    private static final Logger log = LoggerFactory.getLogger(CommonControllerErrorHandlers.class);

    private Counter invalidRequestsCounter = Metrics.counter("errors.request.invalid");
    private Counter forbiddenRequestsCounter = Metrics.counter("errors.lookup.forbidden");

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<String> handleValidationException(ConstraintViolationException ex) {
        invalidRequestsCounter.increment();
        return logAndRespond(ex, BAD_REQUEST);
    }

    @ExceptionHandler({ IncompleteRequestException.class })
    public ResponseEntity<String> handleIncompleteRequestException(IncompleteRequestException ex) {
        invalidRequestsCounter.increment();
        return logAndRespond(ex, BAD_REQUEST);
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return logAndRespond(ex, NOT_FOUND);
    }

    @ExceptionHandler({ ForbiddenException.class })
    public ResponseEntity<String> handleForbiddenException(ForbiddenException ex) {
        forbiddenRequestsCounter.increment();
        return logAndRespond(ex, FORBIDDEN);
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public ResponseEntity<String> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        invalidRequestsCounter.increment();
        return logAndRespond(ex, BAD_REQUEST);
    }

    @ExceptionHandler({ OIDCUnauthorizedException.class })
    public ResponseEntity<String> handleOIDCUnauthorizedException(OIDCUnauthorizedException ex) {
        forbiddenRequestsCounter.increment();
        return logAndRespond(ex, FORBIDDEN);
    }

    private ResponseEntity logAndRespond(Exception ex, HttpStatus status) {
        log.warn("Caught error", ex);
        return new ResponseEntity<>(ex.getMessage(), status);
    }
}
