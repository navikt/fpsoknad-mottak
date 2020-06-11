package no.nav.foreldrepenger.mottak.util;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TimeUtil {
    private static final Logger LOG = LoggerFactory.getLogger(TimeUtil.class);

    private TimeUtil() {
    }

    public static LocalDate dato(String dato) {
        return Optional.ofNullable(dato)
                .map(d -> LocalDate.parse(d, ISO_LOCAL_DATE))
                .orElse(null);

    }

    public static void waitFor(long delayMillis) {
        try {
            LOG.trace("Venter i {}ms", delayMillis);
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException("Kunne ikke vente i " + delayMillis + "ms", e);
        }
    }

    public static boolean dateWithinPeriod(LocalDate start, LocalDate end) {
        var now = LocalDate.now();
        if (now.isEqual(start) || now.isEqual(end)) {
            return true;
        }
        return now.isAfter(start) && now.isBefore(end);
    }

    public static LocalDateTime fraDato(Date dato) {
        return Instant.ofEpochMilli(dato.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
