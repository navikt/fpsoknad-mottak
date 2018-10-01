package no.nav.foreldrepenger.mottak.pdf;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.innsending.pdf.EngangsstønadPDFGenerator;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, EngangsstønadPDFGenerator.class,
        SpringOIDCRequestContextHolder.class })

public class EngangsstønadPDFGeneratorTest {

    @Autowired
    EngangsstønadPDFGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(hasPdfSignature(gen.generate(engangssøknad(true), person())));
    }
}
