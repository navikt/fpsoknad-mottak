package no.nav.foreldrepenger.oppslag.http;

import java.time.LocalDate;

import javax.inject.Inject;
import javax.validation.Valid;

import no.nav.security.spring.oidc.validation.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;

@RestController
@Validated
class InntektController {

    private final InntektClient inntektClient;

    @Inject
    public InntektController(InntektClient inntektClient) {
        this.inntektClient = inntektClient;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/income")
    @Protected
    public ResponseEntity<?> incomeForAktor(@Valid @RequestParam("fnr") Fodselsnummer fnr) {
        LocalDate tenMonthsAgo = LocalDate.now().minusMonths(10);
        return ResponseEntity.ok(inntektClient.incomeForPeriod(fnr, tenMonthsAgo, LocalDate.now()));
    }
}
