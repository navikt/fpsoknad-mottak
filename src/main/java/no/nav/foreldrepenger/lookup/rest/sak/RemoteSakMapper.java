package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RemoteSakMapper {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static Sak map(RemoteSak remoteSak) {
        return new Sak(Integer.toString(remoteSak.getId()),
            remoteSak.getTema(),
            null,
            "FOR",
            remoteSak.getApplikasjon(),
            remoteSak.getFagsakNr(),
            LocalDate.parse(remoteSak.getOpprettetTidspunkt(), formatter));
    }

}
