package no.nav.foreldrepenger.mottak.innsending.innsyn;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.InntektsmeldingXMLMapper;

class InntektsmeldingTest {

    @Test
    void testFørstegangssøknadRoundtrip() throws IOException {
        String xml = StreamUtils.copyToString(new ClassPathResource("xml/inntektsmelding.xml").getInputStream(),
                Charset.defaultCharset());
        InntektsmeldingXMLMapper.tilInntektsmelding(xml);
    }

}
