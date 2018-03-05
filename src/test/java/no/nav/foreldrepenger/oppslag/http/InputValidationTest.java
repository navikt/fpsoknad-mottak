package no.nav.foreldrepenger.oppslag.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.oppslag.OppslagApplicationLocal;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OppslagApplicationLocal.class)
@Tag("slow")
public class InputValidationTest {

   @Autowired
   private TestRestTemplate restTemplate;

   @ParameterizedTest
   @MethodSource("valueProvider")
   @Tag("slow")
   public void fnrMustBe11Chars(String urlBase) {
      String url = urlBase + "/?fnr=1234567890";
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
   }

   @Test
   @Tag("slow")
   public void aktørIdCannotBeMissing() {
      String url = "/fpsak/?missing=aktør";
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
   }

   static Stream<Arguments> valueProvider() {
      return Stream.of(
         Arguments.of("/aareg"),
         Arguments.of("/arena"),
         Arguments.of("/infotrygd"),
         Arguments.of("/income"),
         Arguments.of("/medl"),
         Arguments.of("/person"),
         Arguments.of("/oppslag"));
   }

}
