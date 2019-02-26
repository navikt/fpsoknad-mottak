package no.nav.foreldrepenger.mottak.util;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StreamUtil {

    private static final Logger LOG = LoggerFactory.getLogger(StreamUtil.class);

    private StreamUtil() {
    }

    public static <T> Stream<T> safeStream(List<T> list) {
        return Optional.ofNullable(list).orElse(emptyList()).stream();
    }

    public static <T> List<T> distinct(List<T> list) {
        return safeStream(list).distinct().collect(toList());
    }

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

    public static <T> Collector<T, ?, T> toSingleton() {
        return collectingAndThen(
                toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                });
    }
}
