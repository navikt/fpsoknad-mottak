package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.domain.*;
import no.nav.foreldrepenger.oppslag.orchestrate.CoordinatedLookup;
import no.nav.foreldrepenger.oppslag.person.PersonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Validated
@RequestMapping("/oppslag")
public class OppslagController {

   @Inject
   private PersonClient personClient;
   @Inject
   private AktorIdClient aktorClient;
   @Inject
   private CoordinatedLookup personInfo;

   @GetMapping(value = "/")
   public ResponseEntity<SøkerInformasjon> oppslag(
      @Valid @RequestParam(value = "fnr") Fodselsnummer fnr) {
      AktorId aktorid = aktorClient.aktorIdForFnr(fnr);
      Person person = personClient.hentPersonInfo(new ID(aktorid, fnr));
      AggregatedLookupResults results = personInfo.gimmeAllYouGot(new ID(aktorid, fnr));
      return new ResponseEntity<>(
         new SøkerInformasjon(
               person,
               results.getInntekt(),
               results.getYtelser(),
               results.getArbeidsforhold(),
               results.getMedlPerioder()),
         OK);
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " [personClient=" + personClient + ", aktorClient=" + aktorClient
         + ", personInfo=" + personInfo
         + "]";
   }

}
