package no.nav.foreldrepenger.lookup.rest.sak;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RemoteSakMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private RemoteSakMapper() {

    }

    public static Sak map(RemoteSak remoteSak) {
        return new Sak(Integer.toString(remoteSak.getId()),
                remoteSak.getTema(),
                remoteSak.getApplikasjon(),
                remoteSak.getFagsakNr(),
                "",
                LocalDate.parse(stripSubSecondPart(remoteSak.getOpprettetTidspunkt()), FORMATTER),
                remoteSak.getOpprettetAv()
            );
    }

    private static String stripSubSecondPart(String orig) {
        return orig.substring(0, orig.indexOf((".")));
    }

}
