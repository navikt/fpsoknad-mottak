package no.nav.foreldrepenger.oppslag.infotrygd;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.InfotrygdSakV1;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.InfotrygdSak;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.meldinger.FinnSakListeRequest;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.meldinger.FinnSakListeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static no.nav.foreldrepenger.oppslag.infotrygd.TestdataProvider.sak;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("slow")
public class InfotrygdEndpointTest {

   @MockBean
   private InfotrygdSakV1 infotrygdSakV1;

   @BeforeEach
   public void setup() throws Exception {
      FinnSakListeResponse response = new FinnSakListeResponse();
      InfotrygdSak sak = sak();
      response.getSakListe().add(sak);
      when(infotrygdSakV1.finnSakListe(any(FinnSakListeRequest.class)))
         .thenReturn(response);
   }

   @Autowired
   private TestRestTemplate restTemplate;

   @Test
   public void makeHttpRequestAndDeserializeResult() {
      Ytelse[] ytelser = restTemplate.getForObject("/infotrygd/?fnr=12345678910", Ytelse[].class);
      assertEquals(1, ytelser.length);
   }

}
