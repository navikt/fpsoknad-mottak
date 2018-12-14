package no.nav.foreldrepenger.mottak.pdf;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.V1;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepengeSøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttIkkeOpplastedVedlegg;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.config.TestConfig;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengeInfoRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.PDFElementRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.SøknadTextFormatter;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;
import wiremock.com.google.common.base.CharMatcher;
import wiremock.com.google.common.base.Splitter;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, SøknadTextFormatter.class, ForeldrepengeInfoRenderer.class,
        PDFElementRenderer.class,
        ForeldrepengerPDFGenerator.class,
        SpringOIDCRequestContextHolder.class, TestConfig.class })

@ActiveProfiles("dev")
public class ForeldrepengerPDFGeneratorTest {

    @Autowired
    ForeldrepengerPDFGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(hasPdfSignature(gen.generate(foreldrepengeSøknad(Versjon.V1), person())));
    }

    @Test
    public void førstegangssøknad() throws Exception {

        try (FileOutputStream fos = new FileOutputStream("søknad.pdf")) {
            fos.write(gen.generate(søknadMedEttIkkeOpplastedVedlegg(Versjon.V1, true), person(), arbeidsforhold()));
        }
    }

    @Test
    public void endring() throws Exception {

        try (FileOutputStream fos = new FileOutputStream("endring.pdf")) {
            fos.write(gen.generate(endringssøknad(Versjon.V1, V1), person()));
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

    @Test
    public void lines() {
        for (String line : split("Dette er en lang linje, faktisk akkurat 47 tegn", 20)) {
            System.out.println("Linje er: " + line + " (" + line.length() + ")");
        }
    }

    private static List<String> split(String original, int max) {
        if (original.length() < max) {
            return Collections.singletonList(original);
        }
        List<String> alleOrd = Splitter.on(CharMatcher.whitespace()).splitToList(original);
        int lengde = 0;
        int antallOrd = 0;
        for (String ord : alleOrd) {
            lengde += ord.length() + 1;
            if (lengde >= max) {
                break;
            }
            antallOrd++;
        }
        List<String> linjer = Lists
                .newArrayList(Joiner.on(' ')
                        .join(alleOrd.stream()
                                .limit(antallOrd)
                                .collect(Collectors.toList())));
        linjer.addAll(split(Joiner.on(' ')
                .join(alleOrd.subList(alleOrd.size() - (alleOrd.size() - antallOrd), alleOrd.size())), max));
        return linjer;
    }
}
