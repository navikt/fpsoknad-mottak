package no.nav.foreldrepenger.mottak.innsending.varsel;

import static java.time.LocalDateTime.now;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.DATO;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.FORNAVN;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.URL;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.URL_VALUE;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.VARSEL_TYPE;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.formattertDato;
import static no.nav.foreldrepenger.mottak.innsending.varsel.VarselXMLGenerator.formattertNavn;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.util.jaxb.V1VarselJAXBUtil;
import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.Parameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;

class VarselXMLGeneratorTest {
    @Test
    void testVarselXMLRoundtrip() {
        var jaxb = new V1VarselJAXBUtil(true);
        var varselXmlGenerator = new VarselXMLGenerator(jaxb);
        var varsel = varsel();
        var v = jaxb.unmarshalToElement(varselXmlGenerator.tilXml(varsel), Varsel.class).getValue();
        assertEquals(AktoerId.class.cast(v.getMottaker()).getAktoerId(), varsel.søker().getAktørId().getId());
        assertEquals(VARSEL_TYPE, v.getVarslingstype().getValue());
        var parametre = v.getParameterListe();
        assertEquals(3, parametre.size());
        assertParameter(parametre, DATO, formattertDato(varsel.dato()));
        assertParameter(parametre, FORNAVN, formattertNavn(varsel.søker().getFornavn()));
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
