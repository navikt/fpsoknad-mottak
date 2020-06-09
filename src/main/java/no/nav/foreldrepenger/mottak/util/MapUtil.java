package no.nav.foreldrepenger.mottak.util;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class MapUtil {

    private MapUtil() {
    }

    public static String get(Map<?, ?> map, String key) {
        return get(map, key, String.class);
    }

    public static <T> T get(Map<?, ?> map, String key, Class<T> clazz) {
        return Optional.ofNullable(map)
                .map(m -> m.get(key))
                .filter(Objects::nonNull)
                .map(v -> (T) v)
                .orElse(null);
    }
}
