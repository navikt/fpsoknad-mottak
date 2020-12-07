package no.nav.foreldrepenger.mottak.util;

import java.util.Arrays;

public enum AuthenticationLevel {
    NONE,
    LEVEL3,
    LEVEL4;

    public static AuthenticationLevel of(String level) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(level))
                .findFirst()
                .orElse(NONE);
    }
}
