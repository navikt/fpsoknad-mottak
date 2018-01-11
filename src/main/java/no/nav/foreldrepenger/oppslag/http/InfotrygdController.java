package no.nav.foreldrepenger.oppslag.http;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.NotFoundException;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdClient;

@RestController
class InfotrygdController {

	private final InfotrygdClient infotrygdClient;

	@Inject
	public InfotrygdController(InfotrygdClient infotrygdClient) {
		this.infotrygdClient = infotrygdClient;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = "/infotrygd")
	public ResponseEntity<List<Ytelse>> casesFor(@RequestParam("fnr") String fnr) {
      LocalDate now = LocalDate.now();
      LocalDate oneYearAgo = LocalDate.now().minusMonths(12);
		try {
			return ResponseEntity.ok(infotrygdClient.casesFor(fnr, oneYearAgo, now));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

   @ExceptionHandler({ NotFoundException.class })
   public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
   }

   @ExceptionHandler({ ForbiddenException.class })
   public ResponseEntity<String> handleNotPermittedException(NotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), FORBIDDEN);
   }
}
