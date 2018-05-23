package no.nav.foreldrepenger.mottak.pdf;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.domain.TestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, ForeldrepengerPDFGenerator.class })

public class ForeldrepengerPDFGeneratorTest {

    @Autowired
    ForeldrepengerPDFGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(TestUtils.hasPdfSignature(gen.generate(ForeldrepengerTestUtils.søknad())));
    }
}
