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

    public static String limit(byte[] bytes, int max) {
        return limit(Arrays.toString(bytes), max);
    }
}
