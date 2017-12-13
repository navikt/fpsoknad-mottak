package no.nav.foreldrepenger.selvbetjening;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.selvbetjening.inntekt.domain.Income;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.AktoerId;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Uttrekksperiode;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeRequest;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeResponse;

@Component
public class InntektClient {
   private final InntektV3 inntektV3;

   @Inject
   public InntektClient(InntektV3 inntektV3) {
      this.inntektV3 = inntektV3;
   }

   public List<Income> incomeForPeriod(String aktoerId, LocalDate from, LocalDate to) throws Exception {
      HentInntektListeRequest req = request(aktoerId, from, to);
      HentInntektListeResponse res = inntektV3.hentInntektListe(req);
      return res.getArbeidsInntektIdent().getArbeidsInntektMaaned().stream()
         .flatMap(aim -> aim.getArbeidsInntektInformasjon().getInntektListe().stream())
         .map(InntektMapper::map)
         .collect(toList());
   }

   private HentInntektListeRequest request(String aktoerId, LocalDate from, LocalDate to) {
      HentInntektListeRequest req = new HentInntektListeRequest();
      AktoerId aktoer = new AktoerId();
      aktoer.setAktoerId(aktoerId);
      req.setIdent(aktoer);
      Uttrekksperiode periode = new Uttrekksperiode();
      periode.setMaanedFom(CalendarConverter.toCalendar(from));
      periode.setMaanedTom(CalendarConverter.toCalendar(to));
      req.setUttrekksperiode(periode);
      return req;
   }
}
