package no.nav.foreldrepenger.mottak.util;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class StreamUtil {

    private StreamUtil() {
    }

    public static <T> Stream<T> safeStream(List<T> list) {
        return Optional.ofNullable(list).orElse(emptyList()).stream();
    }

    public static <T> List<T> distinct(List<T> list) {
        return safeStream(list).distinct().collect(toList());
    }
}
