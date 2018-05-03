package no.nav.foreldrepenger.oppslag.http.lookup.ytelser.infotrygd;

import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.Ytelse;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.InfotrygdSak;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InfotrygdsakMapperTest {

    @Test
    @Tag("fast")
    public void mapValues() throws Exception {
        InfotrygdSak sak = TestdataProvider.sak();
        LocalDate date = LocalDate.of(2017, 12, 13);
        Ytelse expected = new Ytelse("typen", "statusen", date, Optional.empty());
        Ytelse actual = InfotrygdsakMapper.map(sak);
        assertEquals(expected, actual);
    }

}
