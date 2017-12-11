package no.nav.foreldrepenger.selvbetjening;

import java.util.Optional;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.Fodselsnummer;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentResponse;

public class AktorIdKlient {
   private static final Logger log = LoggerFactory.getLogger(AktorIdKlient.class);

   private final AktoerV2 aktoerV2;

   public AktorIdKlient() {
      aktoerV2 = new CXFClient<>(AktoerV2.class)
         .configureStsForSystemUser()
         .address("https://xxxxx.test.local")
         .build();
   }

   public Optional<String> hentAktoerId(Fodselsnummer fnr) throws HentAktoerIdForIdentPersonIkkeFunnet {
      try {
         HentAktoerIdForIdentRequest request = lagAktorForIndentRequest(fnr.getValue());
         HentAktoerIdForIdentResponse response = aktoerV2.hentAktoerIdForIdent(request);
         return Optional.ofNullable(response.getAktoerId());
      } catch (SOAPFaultException exception) {
         log.warn("Henting av aktørid har feilet", exception);
         return Optional.empty();
      }
   }

   private HentAktoerIdForIdentRequest lagAktorForIndentRequest(String indent) {
      HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest = new HentAktoerIdForIdentRequest();
      hentAktoerIdForIdentRequest.setIdent(indent);
      return hentAktoerIdForIdentRequest;
   }

}
