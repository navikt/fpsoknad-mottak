package no.nav.foreldrepenger.selvbetjening.controllers;


import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.selvbetjening.AktorIdKlient;
import no.nav.foreldrepenger.selvbetjening.domain.AktorId;
import no.nav.foreldrepenger.selvbetjening.domain.BrukerInformasjon;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;

@RestController
@RequestMapping("/startup")
public class StartupController {	

  private static final Logger LOG = LoggerFactory.getLogger(StartupController.class);
   private final AktorIdKlient aktorClient;

   @Inject
   public  StartupController(AktorIdKlient aktorClient) {
      this.aktorClient = aktorClient;
   }

   @RequestMapping(method = {RequestMethod.GET}, value = "/")
   public ResponseEntity<BrukerInformasjon> startup(@RequestParam("fnr") String fnr) {
	  LOG.info("Looking up {}",fnr);
	  Optional<AktorId> aktorId = aktorClient.aktorIdForFnr(new Fodselsnummer(fnr));
	  if (aktorId.isPresent()) {
          return new ResponseEntity<BrukerInformasjon>(new BrukerInformasjon(aktorId.get(),new Fodselsnummer(fnr)), HttpStatus.OK);
      }
	  return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }
   
   @Override
	public String toString() {
		return getClass().getSimpleName() + " [aktorClient=" + aktorClient + "]";
	}

}
