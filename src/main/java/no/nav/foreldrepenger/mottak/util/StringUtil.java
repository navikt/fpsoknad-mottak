package no.nav.foreldrepenger.mottak.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.CollectionUtils;

public final class StringUtil {

    private StringUtil() {

    }

    public static String endelse(List<?> liste) {
        if (CollectionUtils.isEmpty(liste)) {
            return "er";
        }
        return liste.size() == 1 ? "" : "er";
    }

    public static String fraBytes(byte[] bytes, int max) {
        String asString = Arrays.toString(bytes);
        if (asString.length() >= max) {
            return asString.substring(0, max - 1) + ".... " + (asString.length() - max) + " flere bytes";
        }
        return asString;
    }
}
