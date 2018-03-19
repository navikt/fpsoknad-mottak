package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.OppslagApplicationLocal;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OppslagApplicationLocal.class)
@Tag("slow")
public class InputValidationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Tag("slow")
    public void aktørIdCannotBeMissing() {
        String url = "/fpsak/?missing=aktør";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
