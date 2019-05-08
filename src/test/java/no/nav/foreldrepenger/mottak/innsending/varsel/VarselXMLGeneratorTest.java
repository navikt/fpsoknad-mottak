package no.nav.foreldrepenger.mottak.innsending.varsel;

import static java.time.LocalDateTime.now;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.DATO;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.FORNAVN;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.URL;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.URL_VALUE;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.VARSEL_TYPE;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.formattertDato;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.formattertNavn;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.util.jaxb.V1VarselJAXBUtil;
import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.Parameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;

class VarselXMLGeneratorTest {
    @Test
    void testVarselXMLRoundtrip() throws IOException {
        V1VarselJAXBUtil jaxb = new V1VarselJAXBUtil(true);
        VarselXMLGenerator varselXmlGenerator = new VarselXMLGenerator(jaxb);
        no.nav.foreldrepenger.mottak.innsending.varsel.Varsel varsel = varsel();
        Varsel v = jaxb.unmarshalToElement(varselXmlGenerator.tilXml(varsel), Varsel.class).getValue();
        assertEquals(AktoerId.class.cast(v.getMottaker()).getAktoerId(), varsel.getSøker().getAktørId().getId());
        assertEquals(VARSEL_TYPE, v.getVarslingstype().getValue());
        List<Parameter> parametre = v.getParameterListe();
        assertEquals(3, parametre.size());
        assertParameter(parametre, DATO, formattertDato(varsel.getDato()));
        assertParameter(parametre, FORNAVN, formattertNavn(varsel.getSøker().getFornavn()));
        assertParameter(parametre, URL, URL_VALUE);
    }

    private static void assertParameter(List<Parameter> parameters, String key, String expected) {
        assertEquals(expected, parameters.stream()
                .filter(s -> s.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fant ikke " + key)).getValue());
    }

    private static no.nav.foreldrepenger.mottak.innsending.varsel.Varsel varsel() {
        return new no.nav.foreldrepenger.mottak.innsending.varsel.Varsel(
                now(), person());
    }
}
