package no.nav.foreldrepenger.selvbetjening;

import java.time.LocalDate;

import no.nav.foreldrepenger.selvbetjening.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.AktoerId;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeRequest;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeResponse;

public class InntektClient {
   private final InntektV3 inntektV3;

   public InntektClient() {
      inntektV3 = new CXFClient<>(InntektV3.class)
         .configureStsForSystemUser()
         .address("https://xxxxx.test.local")
         .build();
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
