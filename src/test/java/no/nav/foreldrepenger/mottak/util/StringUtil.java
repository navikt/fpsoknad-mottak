package no.nav.foreldrepenger.mottak.util;

import java.util.List;

public final class StringUtil {

    private StringUtil() {

    }

    public static String endelse(List<?> liste) {
        return liste.size() == 1 ? "" : "er";
    }

}
