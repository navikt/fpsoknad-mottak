package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.time.DateUtil;
import no.nav.tjeneste.virksomhet.sak.v2.WSSak;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GsakMapperTest {

    @Test
    public void mapValues() {
        WSSak sak = new WSSak();
        sak.setSakstype("typen");
        sak.setOpprettelsetidspunkt(DateUtil.toXMLGregorianCalendar(LocalDate.of(2017, 12, 13)));
        LocalDate date = LocalDate.of(2017, 12, 13);
        Ytelse expected = new Ytelse("typen", "ukjent", date, Optional.empty());
        Ytelse actual = GsakMapper.map(sak);
        assertEquals(expected, actual);
    }

}
