package no.nav.foreldrepenger.mottak.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TimeUtil {
    private static final Logger LOG = LoggerFactory.getLogger(TimeUtil.class);

    private TimeUtil() {
    }

    public static void waitFor(long delayMillis) {
        try {
            LOG.trace("Venter i {}ms", delayMillis);
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException("Kunne ikke vente i " + delayMillis + "ms", e);
        }
    }
}
