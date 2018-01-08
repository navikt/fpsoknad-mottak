package no.nav.foreldrepenger.http;

import java.time.LocalDate;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.inntekt.InntektClient;

@RestController
class InntektController {

	private final InntektClient inntektClient;

	@Inject
	public InntektController(InntektClient inntektClient) {
		this.inntektClient = inntektClient;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = "/income")
	public ResponseEntity<?> incomeForAktor(@RequestParam("fnr") String fnr) {
		LocalDate now = LocalDate.now();
		LocalDate tenMonthsAgo = now.minusMonths(10);
		try {
			return ResponseEntity.ok(inntektClient.incomeForPeriod(fnr, tenMonthsAgo, now));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}

	}
}
