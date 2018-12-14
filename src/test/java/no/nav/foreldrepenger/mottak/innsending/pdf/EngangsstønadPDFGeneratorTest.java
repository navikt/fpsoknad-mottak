package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.util.Versjon;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, SøknadTextFormatter.class, EngangsstønadPDFGenerator.class,
        PDFElementRenderer.class })

public class EngangsstønadPDFGeneratorTest {

    @Autowired
    EngangsstønadPDFGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(hasPdfSignature(gen.generate(engangssøknad(Versjon.V1, true), person())));
    }

    @Test
    public void space() throws Exception {

        try (FileOutputStream fos = new FileOutputStream("engangssøknad.pdf")) {
            fos.write(gen.generate(engangssøknad(Versjon.V1, true), person()));
        }
    }
}
