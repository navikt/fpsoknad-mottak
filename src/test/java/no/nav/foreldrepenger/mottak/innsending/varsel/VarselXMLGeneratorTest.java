package no.nav.foreldrepenger.mottak.innsending.varsel;

import com.google.common.io.CharStreams;
import no.nav.foreldrepenger.mottak.util.jaxb.VarselJaxbUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    VarselJaxbUtil.class,
    VarselXMLGenerator.class})
class VarselXMLGeneratorTest {
    @Autowired
    VarselJaxbUtil jaxb;
    @Autowired
    VarselXMLGenerator varselXmlGenerator;

    @Test
    void tilXml() throws IOException {
        LocalDateTime dateTime = LocalDateTime.of(2019, 2, 18, 14, 1, 35);
        String actualXml = varselXmlGenerator.tilXml(person(), dateTime);
        InputStream streamExpectedXml = new ClassPathResource("varseltjeneste/xsd/varsel-v1.xml").getInputStream();
        String expectedXml;

        try (final Reader reader = new InputStreamReader(streamExpectedXml)) {
            expectedXml = CharStreams.toString(reader);
        }

        assertEquals(expectedXml, actualXml);
    }
}
