package no.nav.foreldrepenger.selvbetjening;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.ForeldrepengesakV1;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.meldinger.FinnSakListeRequest;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.meldinger.FinnSakListeResponse;

@Component
public class FpsakClient {
   private static final Logger log = LoggerFactory.getLogger(FpsakClient.class);

   private final ForeldrepengesakV1 fpsakV1;

   @Inject
   public FpsakClient(ForeldrepengesakV1 fpsakV1) {
      this.fpsakV1 = fpsakV1;
   }

   public boolean hasApplications(String aktoerId) {
      FinnSakListeRequest req = new FinnSakListeRequest();
      Aktoer aktoer = new Aktoer();
      aktoer.setAktoerId(aktoerId);
      req.setSakspart(aktoer);
      try {
         FinnSakListeResponse response = fpsakV1.finnSakListe(req);
         return ! response.getSakListe().isEmpty();
      } catch (Exception ex) {
         log.warn("Error while reading from fpsak", ex);
         throw new RuntimeException("Error while reading from fpsak: " + ex.getMessage());
      }
   }

}
