package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.VEDLEGG1;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepengeSøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.svp;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttIkkeOpplastedVedlegg;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.config.TestConfig;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { MottakConfiguration.class, SøknadTextFormatter.class, ForeldrepengeInfoRenderer.class,
        PDFElementRenderer.class,
        DelegerendePDFGenerator.class,
        ForeldrepengerPDFGenerator.class,
        EngangsstønadPDFGenerator.class,
        SvangerskapspengerPDFGenerator.class,
        SvangerskapspengerInfoRenderer.class,
        SpringOIDCRequestContextHolder.class, TestConfig.class })

public class PDFGeneratorTest {

    private static final String TILLEGGSOPPLYSNINGER = "Begrunnelse for å søke om utsettelse, " +
            "på grunn av sykdom tilbake i tid: Jeg var innlagt på sykehus og hadde ingen " +
            "mulighet til å søke om utsettelse.";
    @Autowired
    @Qualifier(DELEGERENDE)
    PDFGenerator gen;

    @Test
    public void signature() {
        assertTrue(hasPdfSignature(
                gen.generate(foreldrepengeSøknad(DEFAULT_VERSJON), person(), INITIELL_FORELDREPENGER)));
    }

    @Test
    public void førstegangssøknad() throws Exception {
        try (FileOutputStream fos = new FileOutputStream("søknad.pdf")) {
            Søknad søknad = søknadMedEttIkkeOpplastedVedlegg(DEFAULT_VERSJON, true);
            søknad.setTilleggsopplysninger(TILLEGGSOPPLYSNINGER);
            fos.write(gen.generate(søknad, person(), INITIELL_FORELDREPENGER));
        }
    }

    @Test
    public void endring() throws Exception {
        try (FileOutputStream fos = new FileOutputStream("endring.pdf")) {
            Endringssøknad endringssøknad = endringssøknad(DEFAULT_VERSJON, VEDLEGG1);
            endringssøknad.setTilleggsopplysninger(TILLEGGSOPPLYSNINGER);
            fos.write(gen.generate(endringssøknad, person(), ENDRING_FORELDREPENGER));
        }
    }

    @Test
    public void engangs() throws Exception {
        try (FileOutputStream fos = new FileOutputStream("engangssøknad.pdf")) {
            fos.write(gen.generate(engangssøknad(DEFAULT_VERSJON, fødsel(), true), person(), INITIELL_ENGANGSSTØNAD));
        }
    }

    @Test
    public void svanger() throws Exception {
        try (FileOutputStream fos = new FileOutputStream("svangerskapspenger.pdf")) {
            fos.write(gen.generate(svp(), person(), INITIELL_SVANGERSKAPSPENGER));
        }
    }
}
