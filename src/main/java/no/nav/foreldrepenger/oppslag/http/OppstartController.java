package no.nav.foreldrepenger.oppslag.http;

import static org.springframework.http.HttpStatus.OK;

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
import no.nav.foreldrepenger.oppslag.domain.Benefit;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Income;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.Pair;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.domain.SøkerInformasjon;
import no.nav.foreldrepenger.oppslag.orchestrate.CoordinatedLookup;
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
   private CoordinatedLookup lookup;

	@GetMapping(value = "/")
	public ResponseEntity<SøkerInformasjon> oppstart(
	        @Valid @RequestParam(value = "fnr", required = true) Fodselsnummer fnr) {
		AktorId aktorid = aktorClient.aktorIdForFnr(fnr);
		Person person = personClient.hentPersonInfo(new ID(aktorid, fnr));
      Pair<List<LookupResult<Income>>, List<LookupResult<Benefit>>> info = lookup.gimmeAllYouGot(new ID(aktorid, fnr));
		return new ResponseEntity<SøkerInformasjon>(
		   new SøkerInformasjon(person, info.getFirst(), info.getSecond()), OK);
	}

   @Override
   public String toString() {
      return "OppstartController{" +
         "personClient=" + personClient +
         ", aktorClient=" + aktorClient +
         ", lookup=" + lookup +
         '}';
   }
}
