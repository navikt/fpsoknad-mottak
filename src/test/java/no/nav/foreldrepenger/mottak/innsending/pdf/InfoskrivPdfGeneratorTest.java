package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.util.Versjon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileOutputStream;
import java.time.LocalDate;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    MottakConfiguration.class,
    InfoskrivPdfGenerator.class,
    PDFElementRenderer.class,
    SøknadTextFormatter.class
})
class InfoskrivPdfGeneratorTest {
    @Autowired
    InfoskrivPdfGenerator gen;

    @Test
    void testInfoskriv() throws Exception {

        Kvittering kvittering = new Kvittering(LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK);
        kvittering.setFørsteDag(LocalDate.now());
        kvittering.setFørsteInntektsmeldingDag(LocalDate.now().minusWeeks(4));

        Søknad søknad = ForeldrepengerTestUtils.foreldrepengeSøknad(Versjon.DEFAULT_VERSJON);

        try (FileOutputStream fos = new FileOutputStream("infoskriv.pdf")) {
            fos.write(gen.generate(søknad, person(), kvittering));
        }

    }

}
