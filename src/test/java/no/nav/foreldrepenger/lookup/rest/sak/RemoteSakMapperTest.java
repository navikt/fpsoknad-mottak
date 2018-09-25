package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoteSakMapperTest {

    @Test
    public void mapValues() {
        RemoteSak remoteSak = new RemoteSak(1, "temaet", "appen", "aktøren",
            "org123", "fagsakNr", "oppretteren", "2018-09-25");
        Sak expected = new Sak("1", "temaet", null,
            "appen", "fagsakNr", LocalDate.of(2018, 9, 25));
        Sak actual = RemoteSakMapper.map(remoteSak);
        assertEquals(expected, actual);
    }

}
