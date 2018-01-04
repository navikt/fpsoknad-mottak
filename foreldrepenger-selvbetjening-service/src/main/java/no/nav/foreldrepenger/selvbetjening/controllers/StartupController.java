package no.nav.foreldrepenger.selvbetjening.controllers;

import java.util.Optional;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.selvbetjening.AktorIdKlient;
import no.nav.foreldrepenger.selvbetjening.domain.AktorId;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;
import no.nav.foreldrepenger.selvbetjening.domain.ID;
import no.nav.foreldrepenger.selvbetjening.domain.Person;
import no.nav.foreldrepenger.selvbetjening.person.klient.PersonKlient;

@RestController
@Validated
@RequestMapping("/startup")
public class StartupController {

	private static final Logger LOG = LoggerFactory.getLogger(StartupController.class);

	@Inject
	private AktorIdKlient aktorClient;
	@Inject
	private PersonKlient personClient;

	@GetMapping(value = "/")
	public ResponseEntity<Person> startup(@Valid @RequestParam(value = "fnr", required = true) Fodselsnummer fnr) {
		Optional<AktorId> aktorId = aktorClient.aktorIdForFnr(fnr);
		Person person = personClient.hentPersonInfo(new ID(aktorId.get(), fnr));
		return new ResponseEntity<Person>(person, HttpStatus.OK);
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<String> handleValidationException(ConstraintViolationException e) {
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient + "]";
	}

}
