package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileOutputStream;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepengeSøknad;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    MottakConfiguration.class,
    InfoskrivPdfGenerator.class,
    PDFElementRenderer.class,
    SøknadTextFormatter.class
})
class InfoskrivArbeidsgiverPdfGeneratorTest {
    @Autowired
    InfoskrivPdfGenerator gen;

    @Test
    void testInfoskriv() throws Exception {

        try (FileOutputStream fos = new FileOutputStream("infoskriv.pdf")) {
            fos.write(gen.generate(foreldrepengeSøknad(DEFAULT_VERSJON), person()));
        }

    }

}
