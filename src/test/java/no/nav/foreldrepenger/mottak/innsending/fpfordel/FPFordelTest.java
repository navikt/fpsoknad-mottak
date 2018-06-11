package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.time.Duration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
/*
 * @ContextConfiguration(classes = { CallIdGenerator.class,
 * TestFordelConfig.class, FPFordelConfiguration.class,
 * SpringOIDCRequestContextHolder.class, FPFordelConnection.class,
 * FPFordelSøknadSender.class, ObjectMapper.class,
 * FPFordelMetdataGenerator.class, FPFordelKonvoluttGenerator.class,
 * MottakConfiguration.class, ForeldrepengerPDFGenerator.class,
 * FPFordelSøknadGenerator.class })
 */

public class FPFordelTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(8089));

    @Autowired
    private FPFordelSøknadSender sender;

    @Test
    public void exampleTest() throws JsonProcessingException {
        /*
         * stubFor(post(urlEqualTo("/fpfordel/api/dokumentforsendelse"))
         * .withMultipartRequestBody( aMultipart()) .willReturn(aResponse()
         * .withStatus(200) .withHeader(HttpHeaders.LOCATION, "http://www.vg.no")
         * .withBody(kvittering()))); Kvittering kvittering =
         * sender.send(ForeldrepengerTestUtils.foreldrepenger(), TestUtils.person());
         */
        System.out.println("hello");
    }

    private static String kvittering() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new FPFordelPendingKvittering(Duration.ofSeconds(2)));
    }

}
