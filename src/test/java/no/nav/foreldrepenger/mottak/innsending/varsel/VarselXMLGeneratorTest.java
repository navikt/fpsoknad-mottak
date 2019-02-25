package no.nav.foreldrepenger.mottak.innsending.varsel;

import static java.time.LocalDateTime.now;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.DATO;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.FORNAVN;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.URL_FP;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.URL_FP_VALUE;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.VARSEL_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.util.jaxb.VarselJaxbUtil;
import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.Parameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;

class VarselXMLGeneratorTest {

    @Test
    void testVarselXMLRoundtrip() throws IOException {
        VarselJaxbUtil jaxb = new VarselJaxbUtil(true);
        VarselXMLGenerator varselXmlGenerator = new VarselXMLGenerator(jaxb);
        Person person = person();
        Varsel varsel = jaxb.unmarshalToElement(varselXmlGenerator.tilXml(person, now()), Varsel.class).getValue();
        assertEquals(AktoerId.class.cast(varsel.getMottaker()).getAktoerId(), person.aktørId.getId());
        assertEquals(VARSEL_TYPE, varsel.getVarslingstype().getValue());
        List<Parameter> parametre = varsel.getParameterListe();
        assertEquals(3, parametre.size());
        assertParameter(parametre, DATO, VarselXMLGenerator.formattertDato(now()));
        assertParameter(parametre, FORNAVN, person.fornavn);
        assertParameter(parametre, URL_FP, URL_FP_VALUE);
    }

    private static void assertParameter(List<Parameter> parameters, String key, String expected) {
        assertEquals(expected, parameters.stream()
                .filter(s -> s.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Fant ikke " + key)).getValue());
    }
}
