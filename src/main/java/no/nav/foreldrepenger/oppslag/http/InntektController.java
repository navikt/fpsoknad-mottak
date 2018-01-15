package no.nav.foreldrepenger.oppslag.http;

import java.time.LocalDate;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;

@RestController
class InntektController {

	private final InntektClient inntektClient;

	@Inject
	public InntektController(InntektClient inntektClient) {
		this.inntektClient = inntektClient;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = "/income")
	public ResponseEntity<?> incomeForAktor(@RequestParam("fnr") Fodselsnummer fnr) {
		LocalDate tenMonthsAgo = LocalDate.now().minusMonths(10);
		try {
			return ResponseEntity.ok(inntektClient.incomeForPeriod(fnr, tenMonthsAgo, LocalDate.now()));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}

	}
}
