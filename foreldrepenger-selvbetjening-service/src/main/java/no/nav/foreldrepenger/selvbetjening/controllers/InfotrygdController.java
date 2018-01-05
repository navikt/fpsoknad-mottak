package no.nav.foreldrepenger.selvbetjening.controllers;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.selvbetjening.InfotrygdClient;

@RestController
class InfotrygdController {

	private final InfotrygdClient infotrygdClient;

	@Inject
	public InfotrygdController(InfotrygdClient infotrygdClient) {
		this.infotrygdClient = infotrygdClient;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = "/infotrygd")
	public ResponseEntity<?> incomeForAktor(@RequestParam("fnr") String fnr) {
		try {
			return ResponseEntity.ok(infotrygdClient.hasCases(fnr));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}

	}
}
