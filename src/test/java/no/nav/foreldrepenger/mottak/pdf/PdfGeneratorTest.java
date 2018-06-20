package no.nav.foreldrepenger.mottak.pdf;

import static no.nav.foreldrepenger.mottak.domain.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.person;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, EngangsstønadPDFGenerator.class,
        SpringOIDCRequestContextHolder.class })

public class PdfGeneratorTest {

    @Autowired
    EngangsstønadPDFGenerator gen;

    @Test
    public void signature() throws Exception {
        assertTrue(hasPdfSignature(gen.generate(engangssøknad(true), person())));
    }

}
