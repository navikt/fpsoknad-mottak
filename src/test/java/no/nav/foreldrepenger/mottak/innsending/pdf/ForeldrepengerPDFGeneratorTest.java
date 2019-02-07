package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.V1;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepengeSøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttIkkeOpplastedVedlegg;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.config.TestConfig;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { MottakConfiguration.class, SøknadTextFormatter.class, ForeldrepengeInfoRenderer.class,
        PDFElementRenderer.class,
        ForeldrepengerPDFGenerator.class,
        SpringOIDCRequestContextHolder.class, TestConfig.class })

@ActiveProfiles()
public class ForeldrepengerPDFGeneratorTest {

    @Autowired
    ForeldrepengerPDFGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(hasPdfSignature(gen.generate(foreldrepengeSøknad(Versjon.V2), person())));
    }

    @Test
    public void førstegangssøknad() throws Exception {

        try (FileOutputStream fos = new FileOutputStream("søknad.pdf")) {
            Søknad søknad = søknadMedEttIkkeOpplastedVedlegg(Versjon.V2, true);
            søknad.setTilleggsopplysninger("Begrunnelse for å søke om utsettelse, " +
                "på grunn av sykdom tilbake i tid: Jeg var innlagt på sykehus og hadde ingen " +
                "mulighet til å søke om utsettelse.");
            fos.write(gen.generate(søknad, person(), arbeidsforhold()));
        }
    }

    @Test
    public void endring() throws Exception {

        try (FileOutputStream fos = new FileOutputStream("endring.pdf")) {
            Endringssøknad endringssøknad = endringssøknad(Versjon.V1, V1);
            endringssøknad.setTilleggsopplysninger("Begrunnelse for å søke om utsettelse, " +
                "på grunn av sykdom tilbake i tid: Jeg var innlagt på sykehus og hadde ingen " +
                "mulighet til å søke om utsettelse.");
            fos.write(gen.generate(endringssøknad, person()));
        }
    }

    private static List<Arbeidsforhold> arbeidsforhold() {
        return Lists.newArrayList(
                new Arbeidsforhold("1234", "", LocalDate.now().minusDays(200),
                        Optional.of(LocalDate.now()), 90.0, "El caca"),
                new Arbeidsforhold("1234", "", LocalDate.now().minusDays(500),
                        Optional.of(LocalDate.now()), 90.0, "El Bedrifto"),
                new Arbeidsforhold("5678", "", LocalDate.now().minusDays(100),
                        Optional.of(LocalDate.now()), 80.0, "TGD"));

    }
}
