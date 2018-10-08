package no.nav.foreldrepenger.lookup.ws.ytelser.gsak;

import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import no.nav.foreldrepenger.time.DateUtil;
import no.nav.tjeneste.virksomhet.sak.v2.WSSak;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GsakMapperTest {

    @Test
    public void mapValues() {
        WSSak sak = new WSSak();
        sak.setSakstype("typen");
        sak.setFagomrade("omr");
        sak.setSakId("sak1");
        sak.setFagsystem("systemet");
        sak.setFagsystemSakId("fsid1");
        LocalDate now = LocalDate.now();
        sak.setOpprettelsetidspunkt(DateUtil.toXMLGregorianCalendar(now));
        Sak expected = new Sak("sak1", "typen", null, "omr", "systemet",
            "fsid1", now);
        Sak actual = GsakMapper.map(sak);
        assertEquals(expected, actual);
    }

}
