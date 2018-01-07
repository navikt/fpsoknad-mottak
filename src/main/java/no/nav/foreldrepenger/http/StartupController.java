package no.nav.foreldrepenger.http;

import static org.springframework.http.HttpStatus.*;

import java.util.Optional;

import javax.inject.*;
import javax.validation.*;

import org.springframework.http.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

import no.nav.foreldrepenger.aktor.*;
import no.nav.foreldrepenger.domain.*;
import no.nav.foreldrepenger.domain.exceptions.*;
import no.nav.foreldrepenger.person.*;

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
      Optional<Person> personOpt =
         personClient.hentPersonInfo(new ID(aktorClient.aktorIdForFnr(fnr), fnr));
	   return personOpt.map(p -> new ResponseEntity(p, OK))
         .orElse(new ResponseEntity(INTERNAL_SERVER_ERROR));
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
