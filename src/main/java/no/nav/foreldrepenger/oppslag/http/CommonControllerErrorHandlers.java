package no.nav.foreldrepenger.oppslag.http;

import javax.validation.ConstraintViolationException;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.IncompleteRequestException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.NotFoundException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class CommonControllerErrorHandlers {
   private Counter errorCounter = Metrics.counter("errors.unhandled");
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

   @ExceptionHandler({ Exception.class })
   public ResponseEntity<String> handleGeneralException(Exception ex) {
      errorCounter.increment();
      return new ResponseEntity<>("Oops, something went wrong...", INTERNAL_SERVER_ERROR);
   }

}
