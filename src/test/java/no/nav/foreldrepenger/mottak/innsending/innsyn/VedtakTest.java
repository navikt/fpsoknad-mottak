package no.nav.foreldrepenger.mottak.innsending.innsyn;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.XMLStreamVedtakInspektør;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.XMLVedtakInspektør;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.XMLV2VedtakMapper;
import no.nav.foreldrepenger.mottak.util.Versjon;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
class VedtakTest {

    @Inject
    private ObjectMapper mapper;

    @Test
    void testVedtak() throws IOException {
        String xml = TestUtils.load("vedtak.xml");
        XMLVedtakInspektør inspektør = new XMLStreamVedtakInspektør();
        Versjon v = inspektør.inspiser(xml);
        Vedtak vedtak = new XMLV2VedtakMapper().tilVedtak(xml, v);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vedtak));
        System.out.println(vedtak);
    }
}
