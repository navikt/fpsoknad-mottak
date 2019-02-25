package no.nav.foreldrepenger.mottak.innsending.varsel;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.util.jaxb.VarselJaxbUtil;
import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;

@ExtendWith(SpringExtension.class)
class VarselXMLGeneratorTest {

    @Test
    void testGenerateVarselXML() throws IOException {
        LocalDateTime dateTime = LocalDateTime.of(2019, 2, 18, 14, 1, 35);
        VarselJaxbUtil jaxb = new VarselJaxbUtil(true, true);
        VarselXMLGenerator varselXmlGenerator = new VarselXMLGenerator(jaxb);
        Person person = person();
        Varsel varsel = jaxb.unmarshalToElement(varselXmlGenerator.tilXml(person, dateTime), Varsel.class).getValue();
        assertEquals(AktoerId.class.cast(varsel.getMottaker()).getAktoerId(), person.aktørId.getId());
    }
}
