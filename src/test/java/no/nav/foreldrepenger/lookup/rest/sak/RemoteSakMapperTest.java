package no.nav.foreldrepenger.lookup.rest.sak;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoteSakMapperTest {

    @Test
    public void mapValues() {
        RemoteSak remoteSak = new RemoteSak(1, "temaet", "appen", "aktøren",
            "org123", "fagsakNr", "oppretteren", "2018-09-02T10:15:42.659+02:00");
        Sak expected = new Sak("1", "temaet",  "appen","fagsakNr", null, LocalDate.of(2018, 9, 2), "");
        Sak actual = RemoteSakMapper.map(remoteSak);
        assertEquals(expected, actual);
    }

}
