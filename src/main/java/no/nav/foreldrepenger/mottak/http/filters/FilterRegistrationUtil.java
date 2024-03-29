package no.nav.foreldrepenger.mottak.http.filters;

import java.util.Arrays;
import java.util.List;

final class FilterRegistrationUtil {

    private static final String ALWAYS = "/*";

    private FilterRegistrationUtil() {
    }

    static List<String> urlPatternsFor(String... patterns) {
        return Arrays.stream(patterns)
            .map(pattern -> pattern + ALWAYS)
            .toList();
    }

    static List<String> always() {
        return List.of(ALWAYS);
    }
}
