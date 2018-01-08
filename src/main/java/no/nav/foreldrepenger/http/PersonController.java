package no.nav.foreldrepenger.http;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.aktor.AktorIdKlient;
import no.nav.foreldrepenger.domain.Fodselsnummer;
import no.nav.foreldrepenger.domain.ID;
import no.nav.foreldrepenger.domain.Person;
import no.nav.foreldrepenger.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.domain.exceptions.NotFoundException;
import no.nav.foreldrepenger.person.PersonKlient;

@RestController
@Validated
@RequestMapping("/person")
public class PersonController {

	@Inject
	private AktorIdKlient aktorClient;
	@Inject
	private PersonKlient personClient;

	@GetMapping(value = "/")
	public ResponseEntity<Person> person(@Valid @RequestParam(value = "fnr", required = true) Fodselsnummer fnr) {
		Person person = personClient.hentPersonInfo(new ID(aktorClient.aktorIdForFnr(fnr), fnr));
		return new ResponseEntity<Person>(person, OK);
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<String> handleValidationException(ConstraintViolationException e) {
		return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
	}

	@ExceptionHandler({ NotFoundException.class })
	public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
	}

	@ExceptionHandler({ ForbiddenException.class })
	public ResponseEntity<String> handleNotPermittedException(NotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), FORBIDDEN);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient + "]";
	}

}
