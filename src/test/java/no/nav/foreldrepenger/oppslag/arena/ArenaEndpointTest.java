package no.nav.foreldrepenger.oppslag.arena;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.binding.YtelseskontraktV3;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.HentYtelseskontraktListeRequest;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.HentYtelseskontraktListeResponse;
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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("slow")
public class ArenaEndpointTest {

   @MockBean
   private YtelseskontraktV3 ytelseskontraktV3;

   @BeforeEach
   public void setup() throws Exception {
      HentYtelseskontraktListeResponse response = new HentYtelseskontraktListeResponse();
      Ytelseskontrakt kontrakt = new Ytelseskontrakt();
      kontrakt.setYtelsestype("typen");
      kontrakt.setStatus("statusen");
      kontrakt.setFomGyldighetsperiode(CalendarConverter.toXMLGregorianCalendar(LocalDate.now()));
      response.getYtelseskontraktListe().add(kontrakt);
      when(ytelseskontraktV3.hentYtelseskontraktListe(any(HentYtelseskontraktListeRequest.class)))
         .thenReturn(response);
   }

   @Autowired
   private TestRestTemplate restTemplate;

   @Test
   public void makeHttpRequestAndDeserializeResult() {
      Ytelse[] ytelser = restTemplate.getForObject("/arena/?fnr=1234567890", Ytelse[].class);
      assertEquals(1, ytelser.length);
      System.out.println(Arrays.toString(ytelser));
   }

}
