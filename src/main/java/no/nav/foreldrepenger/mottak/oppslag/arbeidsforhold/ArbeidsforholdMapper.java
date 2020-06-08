package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.oppslag.OppslagConnection;
import no.nav.foreldrepenger.mottak.util.Pair;

@Component
class ArbeidsforholdMapper {

    private final OppslagConnection oppslag;

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdMapper.class);

    public ArbeidsforholdMapper(OppslagConnection oppslag) {
        this.oppslag = oppslag;
    }

    Arbeidsforhold map(Map<?, ?> map) {
        var arbeidsgiver = get(map, "arbeidsgiver", Map.class);
        var id = idFra(arbeidsgiver);
        var periode = get(get(map, "ansettelsesperiode", Map.class), "periode", Map.class);
        var fom = dato(get(periode, "fom"));
        var tom = Optional.ofNullable(dato(get(periode, "tom")));

        var a = new Arbeidsforhold(id.getFirst(), id.getSecond(), fom, tom,
                stillingsprosent(get(map, "arbeidsavtaler", List.class)), oppslag.organisasjonsNavn(id.getFirst()));

        LOG.info("Arbeidsforhold er {}", a);
        return a;
    }

    static ProsentAndel stillingsprosent(List<?> avtaler) {
        return avtaler.stream()
                .map(Map.class::cast)
                .filter(ArbeidsforholdMapper::gjeldendeAvtale)
                .map(a -> ArbeidsforholdMapper.get(a, "stillingsprosent", Double.class))
                .filter(Objects::nonNull)
                .map(ProsentAndel::new)
                .findFirst()
                .orElse(null);
    }

    private static boolean gjeldendeAvtale(Map<?, ?> avtale) {
        Map<?, ?> periode = get(avtale, "gyldighetsperiode", Map.class);
        var fom = dato(get(periode, "fom"));
        var tom = Optional.ofNullable(dato(get(periode, "tom")))
                .orElse(LocalDate.now());
        return dateWithinPeriod(now(), fom, tom);
    }

    private static LocalDate dato(String dato) {
        return Optional.ofNullable(dato)
                .map(d -> LocalDate.parse(d, ISO_LOCAL_DATE))
                .orElse(null);

    }

    public static boolean dateWithinPeriod(LocalDate date, LocalDate start, LocalDate end) {
        if (date.isEqual(start) || date.isEqual(end)) {
            return true;
        }
        return date.isAfter(start) && date.isBefore(end);
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
