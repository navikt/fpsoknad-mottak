package no.nav.foreldrepenger.lookup.ws.ytelser.arena;

import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YtelseskontraktMapperTest {

    @Test
    public void mapValues() throws Exception {
        Ytelseskontrakt kontrakt = new Ytelseskontrakt();
        kontrakt.setFomGyldighetsperiode(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-12"));
        kontrakt.setTomGyldighetsperiode(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13"));
        kontrakt.setYtelsestype("typen");
        kontrakt.setStatus("statusen");
        Ytelse expected = new Ytelse(
           "typen",
           "statusen",
           LocalDate.of(2017, 12, 12),
           Optional.of(LocalDate.of(2017, 12, 13)));
        Ytelse actual = YtelseskontraktMapper.map(kontrakt);
        assertEquals(expected, actual);
    }

}
