package no.nav.foreldrepenger.mottak.util;

import org.slf4j.MDC;

public final class MDCUtil {

    private static final String PREFIX = "Nav-";

    private MDCUtil() {

    }

    public static Pair<String, String> getForTransfer(String key) {
        String value = MDC.get(key);
        return value != null ? Pair.of(PREFIX + key, value) : null;
    }

    public static void put(String key, String value) {
        if (key.startsWith(PREFIX)) {
            MDC.put(key.substring(PREFIX.length()), value);
        }
        else {
            MDC.put(key, value);
        }
    }

}
