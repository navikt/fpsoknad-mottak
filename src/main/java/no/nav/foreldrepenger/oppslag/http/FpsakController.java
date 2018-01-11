package no.nav.foreldrepenger.oppslag.http;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.NotFoundException;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakKlient;

@RestController
class FpsakController {

	private final AktorIdClient aktorClient;
   private final FpsakKlient fpsakClient;

	@Inject
	public FpsakController(AktorIdClient aktorClient, FpsakKlient fpsakClient) {
		this.aktorClient = aktorClient;
      this.fpsakClient = fpsakClient;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = "/fpsak")
	public ResponseEntity<List<Ytelse>> casesFor(@RequestParam("fnr") String fnr) {
		try {
         Fodselsnummer f = new Fodselsnummer(fnr);
			return ResponseEntity.ok(fpsakClient.casesFor(aktorClient.aktorIdForFnr(f)));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

   @ExceptionHandler({ ForbiddenException.class })
   public ResponseEntity<String> handleNotPermittedException(NotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), FORBIDDEN);
   }
}
