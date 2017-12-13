package no.nav.foreldrepenger.selvbetjening;

import java.time.LocalDate;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.AktoerId;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeRequest;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeResponse;

@Component
public class InntektClient {
   private final InntektV3 inntektV3;

   @Inject
   public InntektClient(InntektV3 inntektV3) {
      this.inntektV3 = inntektV3;
   }

   public void hentInntektListe(String aktoerId, LocalDate from, LocalDate to) throws Exception {
      HentInntektListeRequest req = new HentInntektListeRequest();
      AktoerId aktoer = new AktoerId();
      aktoer.setAktoerId(aktoerId);
      req.setIdent(aktoer);
      // req.setUttrekksperiode(); mnd fom og tom
      HentInntektListeResponse response = inntektV3.hentInntektListe(req);
   }
}
