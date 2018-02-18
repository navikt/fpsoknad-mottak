package no.nav.foreldrepenger.oppslag.medl;

import no.nav.foreldrepenger.oppslag.domain.MedlPeriode;
import no.nav.tjeneste.virksomhet.medlemskap.v2.MedlemskapV2;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.Medlemsperiode;
import no.nav.tjeneste.virksomhet.medlemskap.v2.meldinger.HentPeriodeListeRequest;
import no.nav.tjeneste.virksomhet.medlemskap.v2.meldinger.HentPeriodeListeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static no.nav.foreldrepenger.oppslag.medl.TestdataProvider.medlemsperiode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("slow")
public class MedlEndpointTest {

   @MockBean
   private MedlemskapV2 medlemskapV2;

   @BeforeEach
   public void setup() throws Exception {
      HentPeriodeListeResponse response = new HentPeriodeListeResponse();
      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(3);
      Medlemsperiode periode = medlemsperiode(earlier, now);
      response.getPeriodeListe().add(periode);
      when(medlemskapV2.hentPeriodeListe(any(HentPeriodeListeRequest.class)))
         .thenReturn(response);
   }

   @Autowired
   private TestRestTemplate restTemplate;

   @Test
   public void makeHttpRequestAndDeserializeResult() {
      MedlPeriode[] medlPerioder = restTemplate.getForObject("/medl/?fnr=12345678910", MedlPeriode[].class);
      assertEquals(1, medlPerioder.length);
   }

}
