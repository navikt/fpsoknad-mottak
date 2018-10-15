package no.nav.foreldrepenger.lookup.rest.sak;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;

public class RemoteSakMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private RemoteSakMapper() {

    }

    public static Sak map(RemoteSak remoteSak) {
        return new Sak(Integer.toString(remoteSak.getId()),
                remoteSak.getTema(),
                remoteSak.getApplikasjon(),
                remoteSak.getFagsakNr(),
                "",
                LocalDate.parse(remoteSak.getOpprettetTidspunkt(), FORMATTER));
    }

}
