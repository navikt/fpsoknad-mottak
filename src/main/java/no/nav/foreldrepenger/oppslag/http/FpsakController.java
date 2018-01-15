package no.nav.foreldrepenger.oppslag.http;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
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
      Fodselsnummer f = new Fodselsnummer(fnr);
      return ResponseEntity.ok(fpsakClient.casesFor(aktorClient.aktorIdForFnr(f)));
	}
}
