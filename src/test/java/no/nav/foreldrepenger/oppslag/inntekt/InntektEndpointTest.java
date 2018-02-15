package no.nav.foreldrepenger.oppslag.inntekt;

import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.hentinntektliste.ArbeidsInntektIdent;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.hentinntektliste.ArbeidsInntektInformasjon;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.hentinntektliste.ArbeidsInntektMaaned;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Organisasjon;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Periode;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeRequest;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("slow")
public class InntektEndpointTest {

   @MockBean
   private InntektV3 inntektV3;

   @BeforeEach
   public void setup() throws Exception {
      HentInntektListeResponse response = new HentInntektListeResponse();
      ArbeidsInntektIdent ident = new ArbeidsInntektIdent();
      ArbeidsInntektMaaned mnd = new ArbeidsInntektMaaned();
      ArbeidsInntektInformasjon info = new ArbeidsInntektInformasjon();
      mnd.setArbeidsInntektInformasjon(info);
      info.getInntektListe().add(inntekt());
      mnd.setArbeidsInntektInformasjon(info);
      ident.getArbeidsInntektMaaned().add(mnd);
      response.setArbeidsInntektIdent(ident);

      when(inntektV3.hentInntektListe(any(HentInntektListeRequest.class)))
         .thenReturn(response);
   }

   @Autowired
   private TestRestTemplate restTemplate;

   @Test
   public void makeHttpRequestAndDeserializeResult() {
      Inntekt[] inntekter = restTemplate.getForObject("/income/?fnr=1234567890", Inntekt[].class);
      assertEquals(1, inntekter.length);
   }

   private no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt inntekt() {
      no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt fraOrg =
         new no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt();
      Periode periode = new Periode();
      periode.setStartDato(CalendarConverter.toXMLGregorianCalendar(LocalDate.of(2017, 12, 13)));
      periode.setSluttDato(CalendarConverter.toXMLGregorianCalendar(LocalDate.of(2017, 12, 14)));
      fraOrg.setOpptjeningsperiode(periode);
      fraOrg.setBeloep(BigDecimal.valueOf(1234.5));
      Organisasjon org = new Organisasjon();
      org.setOrgnummer("11111");
      fraOrg.setVirksomhet(org);
      return fraOrg;
   }

}
