package no.nav.foreldrepenger.errorhandling;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;

@ControllerAdvice
public class CommonControllerErrorHandlers {
    private Counter invalidRequestsCounter = Metrics.counter("errors.request.invalid");
    private Counter forbiddenRequestsCounter = Metrics.counter("errors.lookup.forbidden");

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<String> handleValidationException(ConstraintViolationException e) {
        invalidRequestsCounter.increment();
        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({ IncompleteRequestException.class })
    public ResponseEntity<String> handleIncompleteRequestException(IncompleteRequestException e) {
        invalidRequestsCounter.increment();
        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler({ ForbiddenException.class })
    public ResponseEntity<String> handleForbiddenException(ForbiddenException e) {
        forbiddenRequestsCounter.increment();
        return new ResponseEntity<>(e.getMessage(), FORBIDDEN);
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public ResponseEntity<String> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        invalidRequestsCounter.increment();
        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({ OIDCUnauthorizedException.class })
    public ResponseEntity<String> handleOIDCUnauthorizedException(OIDCUnauthorizedException e) {
        forbiddenRequestsCounter.increment();
        return new ResponseEntity<>(e.getMessage(), FORBIDDEN);
    }
}
