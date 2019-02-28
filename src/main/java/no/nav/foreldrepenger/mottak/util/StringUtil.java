package no.nav.foreldrepenger.mottak.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public static String limit(String tekst, int max) {
        return Optional.ofNullable(tekst)
                .filter(t -> t.length() >= max)
                .map(s -> s.substring(0, max - 1))
                .orElse(tekst);
    }

    public static String fraBytes(byte[] bytes, int max) {
        String asString = Arrays.toString(bytes);
        if (asString.length() >= max) {
            return asString.substring(0, max - 1) + ".... " + (asString.length() - max) + " flere bytes";
        }
        return asString;
    }
}
