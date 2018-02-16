package no.nav.foreldrepenger.mottak.pdf;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import no.nav.foreldrepenger.mottak.MottakApplication;
import no.nav.foreldrepenger.mottak.TestUtils;
import no.nav.foreldrepenger.mottak.config.SwaggerConfiguration;
import no.nav.foreldrepenger.mottak.dokmot.DokmotConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { MottakApplication.class, DokmotConfig.class, SwaggerConfiguration.class })
// These are dummy values
@TestPropertySource(properties = { "MQGATEWAY01_HOSTNAME = host", "MQGATEWAY01_PORT: 1412", "MQGATEWAY01_NAME = name",
        "BRISDOKMOT_USERNAME = username", "BRISDOKMOT_PASSWORD = pw", "DOKMOT_CHANNEL_NAME = channel",
        "DOKMOT_MOTTA_FORSENDELSE_DITT_NAV_QUEUENAME = queue" })

public class PdfGeneratorTest {

    @Autowired
    ApplicationContext ctx;

    @Test
    public void smokeTest() throws Exception {

        // TODO Get the generator without starting the whole thing
        byte[] pdf = ctx.getBean(PdfGenerator.class).generate(TestUtils.engangssøknad(true));
        assertTrue(hasPdfSignature(pdf));
    }

    private boolean hasPdfSignature(byte[] bytes) {
        return bytes[0] == 0x25 &&
                bytes[1] == 0x50 &&
                bytes[2] == 0x44 &&
                bytes[3] == 0x46;
    }

}
