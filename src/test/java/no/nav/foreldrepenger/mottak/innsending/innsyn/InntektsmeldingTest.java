package no.nav.foreldrepenger.mottak.innsending.innsyn;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.Inntektsmelding;
import no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.InntektsmeldingXMLMapper;

public class InntektsmeldingTest {

    private InntektsmeldingXMLMapper xmlMapper;

    @BeforeEach
    public void beforeEach() {
        xmlMapper = new InntektsmeldingXMLMapper();
    }

    @Test
    public void testFørstegangssøknadRoundtrip() throws IOException {
        String xml = StreamUtils.copyToString(new ClassPathResource("xml/inntektsmelding.xml").getInputStream(),
                Charset.defaultCharset());
        Inntektsmelding melding = xmlMapper.tilInntektsmelding(xml);
    }

}
