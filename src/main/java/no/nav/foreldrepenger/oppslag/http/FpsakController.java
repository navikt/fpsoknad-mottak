package no.nav.foreldrepenger.oppslag.http;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Benefit;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakKlient;

@RestController
class FpsakController {

	private final FpsakKlient fpsakClient;

	@Inject
	public FpsakController(FpsakKlient fpsakClient) {
		this.fpsakClient = fpsakClient;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = "/fpsak")
	public ResponseEntity<List<Benefit>> casesFor(@RequestParam("aktor") AktorId aktor) {
		return ResponseEntity.ok(fpsakClient.casesFor(aktor));
	}
}
