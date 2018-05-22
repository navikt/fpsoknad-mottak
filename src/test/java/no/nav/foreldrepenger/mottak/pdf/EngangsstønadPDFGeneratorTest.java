package no.nav.foreldrepenger.mottak.pdf;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.domain.TestUtils;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, EngangsstønadPDFGenerator.class })

public class EngangsstønadPDFGeneratorTest {

    @Autowired
    EngangsstønadPDFGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(TestUtils.hasPdfSignature(gen.generate(TestUtils.engangssøknad(true))));
    }
}
