package no.nav.foreldrepenger.lookup.rest.sak;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Format 2017-08-04T23:56:09+02:00 must be handled

public class RemoteSakMapper {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteSakMapper.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private RemoteSakMapper() {

    }

    public static Sak map(RemoteSak remoteSak) {
        return new Sak(Integer.toString(remoteSak.getId()),
                remoteSak.getTema(),
                remoteSak.getApplikasjon(),
                remoteSak.getFagsakNr(),
                "",
                parseSafely(remoteSak.getOpprettetTidspunkt()),
                remoteSak.getOpprettetAv());
    }

    private static LocalDate parseSafely(String opprettetTidspunkt) {
        try {
            return Optional.ofNullable(opprettetTidspunkt)
                    .map(s -> s.substring(0, s.indexOf(".")))
                    .map(s -> LocalDate.parse(s, FORMATTER)).orElse(null);
        } catch (Exception e) {
            LOG.warn("Kunne ikke parse dato {}", opprettetTidspunkt);
            return null;
        }
    }

}
