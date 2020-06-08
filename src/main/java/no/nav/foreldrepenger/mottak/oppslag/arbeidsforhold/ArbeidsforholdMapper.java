package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.util.Pair;

class ArbeidsforholdMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdMapper.class);

    private ArbeidsforholdMapper() {

    }

    static Arbeidsforhold map(Map<?, ?> map) {
        LOG.info("Mapper {}", map);
        var arbeidsgiver = get(map, "arbeidsgiver", Map.class);
        var id = idFra(arbeidsgiver);
        // return new Arbeidsforhold(id.getFirst(), id.getSecond(), from, to,
        // stillingsprosent, arbeidsgiverNavn)
        return null;
    }

    private static Pair<String, String> idFra(Map<?, ?> map) {
        var type = get(map, "type");
        if ("Organisasjon".equals(type)) {
            var orgnr = get(map, "organisasjonsnummer");
            LOG.info("type {} orgnr {}", type, orgnr);
            return Pair.of(orgnr, "orgnr");
        }
        if ("Person".equals(type)) {
            var fnr = get(map, "offentligIdent");
            LOG.info("type {} fnr {}", type, fnr);
            return Pair.of(fnr, "fnr");
        }
        throw new IllegalArgumentException("Fant verken orgnr eller fnr i " + map);

    }

    private static String get(Map<?, ?> map, String key) {
        return get(map, key, String.class);
    }

    private static <T> T get(Map<?, ?> map, String key, Class<T> clazz) {
        LOG.info("Henter {} fra {}", key, map);
        var verdi = Optional.ofNullable(map)
                .map(m -> m.get(key))
                .filter(Objects::nonNull)
                .map(v -> (T) v)
                .orElse(null);
        LOG.info("Hentet verdi {} fra key {} i {}", verdi, key, map);
        return verdi;
    }

}
