package no.nav.foreldrepenger.selvbetjening;

import java.util.Optional;

import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.AktorId;
import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.Fodselsnummer;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentResponse;

@Component
public class AktorIdKlient  implements AktorOperations  {
	   private static final Logger LOGGER = LoggerFactory.getLogger(AktorIdKlient.class);

	   private final AktoerV2 aktoerV2;

	   @Inject
	   public AktorIdKlient(AktoerV2 aktoerV2) {
	     this.aktoerV2 = aktoerV2;
	   }

	   @Override
	   public Optional<AktorId> aktorIdForFnr(Fodselsnummer fnr)  {
	      try {
	         HentAktoerIdForIdentResponse response = aktoerV2.hentAktoerIdForIdent(request(fnr.getValue()));
	         if (response.getAktoerId() != null) {
	                 return Optional.of(new AktorId(response.getAktoerId()));
	                 }
	          return Optional.empty();
	      } catch (SOAPFaultException e) {
	         LOGGER.warn("Henting av aktÃ¸rid har feilet", e);
	         return Optional.empty();
	      } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
	          return Optional.empty();
	        }
	   }
   private HentAktoerIdForIdentRequest request(String indent) {
      HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest = new HentAktoerIdForIdentRequest();
      hentAktoerIdForIdentRequest.setIdent(indent);
      return hentAktoerIdForIdentRequest;
   }

}
