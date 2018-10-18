package no.nav.foreldrepenger.mottak.pdf;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepengeSøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttIkkeOpplastedVedlegg;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.config.TestConfig;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPDFGenerator;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, ForeldrepengerPDFGenerator.class,
        SpringOIDCRequestContextHolder.class, TestConfig.class })

@ActiveProfiles("dev")
public class ForeldrepengerPDFGeneratorTest {

    @Autowired
    ForeldrepengerPDFGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(hasPdfSignature(gen.generate(foreldrepengeSøknad(), person())));
    }

    @Test
    public void space() throws Exception {

        try (FileOutputStream fos = new FileOutputStream("søknad.pdf")) {
            fos.write(gen.generate(søknadMedEttIkkeOpplastedVedlegg(), person(), arbeidsforhold()));
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
