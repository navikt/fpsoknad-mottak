package no.nav.foreldrepenger.mottak.pdf;

import static no.nav.foreldrepenger.mottak.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.TestUtils.hasPdfSignature;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.config.AppConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AppConfig.class, PdfGenerator.class })

public class PdfGeneratorTest {

    @Autowired
    PdfGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(hasPdfSignature(gen.generate(engangssøknad(true))));
    }
}
