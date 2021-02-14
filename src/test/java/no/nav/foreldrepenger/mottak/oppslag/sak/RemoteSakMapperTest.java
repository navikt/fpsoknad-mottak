package no.nav.foreldrepenger.mottak.oppslag.sak;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class RemoteSakMapperTest {

    @Test
    void mapValues() {
        assertEquals(new Sak("1", "temaet", "appen", "fagsakNr", null, LocalDate.of(2018, 9, 2), ""),
                RemoteSakMapper.map(new RemoteSak(1L, "temaet", "appen", "aktøren",
                        "org123", "fagsakNr", "oppretteren", "2018-09-02T10:15:42.659+02:00")));
    }

    @Test
    void alternativeDateFormat() {
        assertEquals(new Sak("1", "temaet", "appen", "fagsakNr", null, LocalDate.of(2018, 8, 27), ""),
                RemoteSakMapper.map(new RemoteSak(1L, "temaet", "appen", "aktøren",
                        "org123", "fagsakNr", "oppretteren", "2018-08-27T09:16:01.2+02:00")));
    }

    @Test
    void alternativeDateFormat1() {
        assertEquals(new Sak("1", "temaet", "appen", "fagsakNr", null, LocalDate.of(2015, 11, 12), ""),
                RemoteSakMapper.map(new RemoteSak(1L, "temaet", "appen", "aktøren",
                        "org123", "fagsakNr", "oppretteren", "2015-11-12T08:19:16+01:00")));
    }

}
