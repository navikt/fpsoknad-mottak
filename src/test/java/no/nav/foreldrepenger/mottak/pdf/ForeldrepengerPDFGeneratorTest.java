package no.nav.foreldrepenger.mottak.pdf;

import static no.nav.foreldrepenger.mottak.domain.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepenger;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, ForeldrepengerPDFGenerator.class,
        SpringOIDCRequestContextHolder.class })

public class ForeldrepengerPDFGeneratorTest {

    @Autowired
    ForeldrepengerPDFGenerator gen;

    @Test
    public void signature() {
        assertTrue(hasPdfSignature(gen.generate(foreldrepenger(), person())));
    }
}
