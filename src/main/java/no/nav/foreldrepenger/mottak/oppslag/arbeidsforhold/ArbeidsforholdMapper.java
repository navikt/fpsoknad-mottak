package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ArbeidsforholdMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdMapper.class);

    private ArbeidsforholdMapper() {

    }

    static Arbeidsforhold map(Map<?, ?> map) {
        LOG.info("Mapper {}", map);
        var type = get(get(map, "arbeidsgiver", Map.class), "type");
        if ("organisasjon".equals(type)) {
            var orgnr = get(map, "organisasjonsnummer");
            LOG.info("type {} orgnr {}", type, orgnr);
        }
        return null;
    }

    private static String get(Map<?, ?> map, String key) {
        return get(map, key, String.class);
    }

    private static <T> T get(Map<?, ?> map, String key, Class<T> clazz) {
        return Optional.ofNullable(map)
                .map(m -> m.get(key))
                .filter(Objects::nonNull)
                .map(v -> (T) v)
                .orElse(null);
    }

}
