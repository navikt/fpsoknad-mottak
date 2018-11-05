package no.nav.foreldrepenger.lookup.rest.sak;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class RemoteSakMapperTest {

    @Test
    public void mapValues() {
        RemoteSak remoteSak = new RemoteSak(1, "temaet", "appen", "aktøren",
                "org123", "fagsakNr", "oppretteren", LocalDateTime.now().minusMonths(1));
        Sak expected = new Sak("1", "temaet", "appen", "fagsakNr", null, LocalDate.now().minusMonths(1), "");
        Sak actual = RemoteSakMapper.map(remoteSak);
        assertEquals(expected, actual);
    }

}
