package no.nav.foreldrepenger.oppslag.fpsak;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Sak;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static no.nav.foreldrepenger.oppslag.fpsak.TestdataProvider.*;

public class SakMapperTest {

    @Test
    @Tag("fast")
    public void mapValues() throws Exception {
        Sak sak = sak();
        Ytelse expected = new Ytelse("temaet",
                "statusen",
                LocalDate.of(2017, 12, 13),
                Optional.empty());
        Ytelse actual = SakMapper.map(sak);
        assertEquals(expected, actual);
    }

}
