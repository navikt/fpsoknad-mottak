package no.nav.foreldrepenger.oppslag.http;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.NotFoundException;

@ControllerAdvice
public class CommonControllerErrorHandlers {

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<String> handleValidationException(ConstraintViolationException e) {
		return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
	}

	@ExceptionHandler({ NotFoundException.class })
	public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
	}

	@ExceptionHandler({ ForbiddenException.class })
	public ResponseEntity<String> handleForbiddenException(ForbiddenException e) {
		return new ResponseEntity<>(e.getMessage(), FORBIDDEN);
	}

}
