package no.nav.foreldrepenger.mottak;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.pdf.PdfGenerator;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, PdfGenerator.class })

public class PdfGeneratorTest {

    @Autowired
    PdfGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(TestUtils.hasPdfSignature(gen.generate(TestUtils.engangssøknad(true))));
    }

    // @Test
    public void far() throws Exception {
        byte[] pdf = gen.generate(TestUtils.engangssøknad(true));
        FileUtils.writeByteArrayToFile(new File("jalla.pdf"), pdf);
    }
}
