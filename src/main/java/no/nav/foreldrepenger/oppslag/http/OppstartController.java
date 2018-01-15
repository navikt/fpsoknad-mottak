package no.nav.foreldrepenger.oppslag.http;

import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Income;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.domain.SøkerInformasjon;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;
import no.nav.foreldrepenger.oppslag.person.PersonKlient;

@RestController
@Validated
@RequestMapping("/oppstart")
public class OppstartController {

	@Inject
	private PersonKlient personClient;
	@Inject
	private AktorIdClient aktorClient;
	@Inject
	InntektClient inntektClient;

	@GetMapping(value = "/")
	public ResponseEntity<SøkerInformasjon> person(
	        @Valid @RequestParam(value = "fnr", required = true) Fodselsnummer fnr) {
		AktorId aktorid = aktorClient.aktorIdForFnr(fnr);
		Person person = personClient.hentPersonInfo(new ID(aktorid, fnr));
		List<Income> inntekt = inntektClient.incomeForPeriod(fnr, LocalDate.now().minus(Period.ofMonths(10)),
		        LocalDate.now());
		return new ResponseEntity<SøkerInformasjon>(new SøkerInformasjon(person, inntekt), OK);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient + "]";
	}

}
