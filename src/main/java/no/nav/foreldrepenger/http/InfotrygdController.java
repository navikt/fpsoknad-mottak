package no.nav.foreldrepenger.http;

import javax.inject.Inject;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import no.nav.foreldrepenger.infotrygd.InfotrygdClient;

@RestController
class InfotrygdController {

	private final InfotrygdClient infotrygdClient;

	@Inject
	public InfotrygdController(InfotrygdClient infotrygdClient) {
		this.infotrygdClient = infotrygdClient;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = "/wsdl/infotrygd")
	public ResponseEntity<?> incomeForAktor(@RequestParam("fnr") String fnr) {
		try {
			return ResponseEntity.ok(infotrygdClient.hasCases(fnr));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}

	}
}
