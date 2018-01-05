package no.nav.foreldrepenger.selvbetjening.controllers;

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

import no.nav.foreldrepenger.selvbetjening.AktorIdKlient;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;
import no.nav.foreldrepenger.selvbetjening.domain.ID;
import no.nav.foreldrepenger.selvbetjening.domain.Person;
import no.nav.foreldrepenger.selvbetjening.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.selvbetjening.domain.exceptions.NotFoundException;
import no.nav.foreldrepenger.selvbetjening.person.klient.PersonKlient;

@RestController
@Validated
@RequestMapping("/startup")
public class StartupController {


	@Inject
	private AktorIdKlient aktorClient;
	@Inject
	private PersonKlient personClient;

	@GetMapping(value = "/")
	public ResponseEntity<Person> startup(@Valid @RequestParam(value = "fnr", required = true) Fodselsnummer fnr) {
		return new ResponseEntity<Person>(personClient.hentPersonInfo(new ID(aktorClient.aktorIdForFnr(fnr), fnr)),OK);
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<String> handleValidationException(ConstraintViolationException e) {
		return new ResponseEntity<String>(e.getMessage(), BAD_REQUEST);
	}

	@ExceptionHandler({ NotFoundException.class })
	public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
		return new ResponseEntity<String>(e.getMessage(), NOT_FOUND);
	}

	@ExceptionHandler({ ForbiddenException.class })
	public ResponseEntity<String> handleNotPermittedException(NotFoundException e) {
		return new ResponseEntity<String>(e.getMessage(), FORBIDDEN);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient + "]";
	}

}
