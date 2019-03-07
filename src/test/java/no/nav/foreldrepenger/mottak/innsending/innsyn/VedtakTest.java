package no.nav.foreldrepenger.mottak.innsending.innsyn;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import no.nav.foreldrepenger.mottak.innsyn.Vedtak;
import no.nav.foreldrepenger.mottak.innsyn.XMLVedtakInspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamVedtakInspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLV2VedtakMapper;
import no.nav.foreldrepenger.mottak.util.Versjon;

class VedtakTest {

    @Test
    void testVedtak() throws IOException {
        String xml = load("vedtak.xml");
        XMLVedtakInspektør inspektør = new XMLStreamVedtakInspektør();
        Versjon v = inspektør.inspiser(xml);
        Vedtak vedtak = new XMLV2VedtakMapper().tilVedtak(xml, v);
    }

    private static String load(String file) throws IOException {
        return copyToString(new ClassPathResource(file).getInputStream(), defaultCharset());
    }

}
